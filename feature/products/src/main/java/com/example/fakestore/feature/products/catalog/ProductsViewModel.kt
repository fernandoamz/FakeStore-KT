package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.model.SearchHistory
import com.example.fakestore.domain.repository.FavoritesRepository
import com.example.fakestore.domain.repository.ProductRepository
import com.example.fakestore.domain.usecase.ClearSearchHistoryUseCase
import com.example.fakestore.domain.usecase.DeleteSearchHistoryUseCase
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import com.example.fakestore.domain.usecase.GetSearchHistoryUseCase
import com.example.fakestore.domain.usecase.GetUserProfileUseCase
import com.example.fakestore.domain.usecase.SaveSearchHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class ProductSlice(
    val catalog: List<Product>,
    val categoryProducts: List<Product>,
    val selectedCategory: String?,
    val searchQuery: String,
    val favorites: Set<Int>
)

private data class MetaSlice(
    val categories: List<String>,
    val isLoadingInitial: Boolean,
    val isLoadingMore: Boolean,
    val isLoadingCategory: Boolean,
    val error: String?,
    val total: Int,
    val userZipcode: String,
    val userAddress: String
)

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val saveSearchHistoryUseCase: SaveSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val deleteSearchHistoryUseCase: DeleteSearchHistoryUseCase,
    private val favoritesRepository: FavoritesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _categoryProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _selectedCategory = savedStateHandle.getStateFlow<String?>("selected_category", null)
    private val _searchQuery = MutableStateFlow("")
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    private val _total = MutableStateFlow(0)
    private val _skip = MutableStateFlow(0)
    private val _limit = 10
    private val _isLoadingInitial = MutableStateFlow(true)
    private val _isLoadingMore = MutableStateFlow(false)
    private val _isLoadingCategory = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _userZipcode = MutableStateFlow("28869")
    private val _userAddress = MutableStateFlow("626 Main Street, Phoenix (CP: 85001)")

    val searchHistory: StateFlow<List<SearchHistory>> = getSearchHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<ProductsUiState> = combine(
        combine(
            _allProducts,
            _categoryProducts,
            _selectedCategory,
            _searchQuery,
            favoritesRepository.favoriteProductIds
        ) { catalog, categoryProducts, selectedCategory, searchQuery, favorites ->
            ProductSlice(catalog, categoryProducts, selectedCategory, searchQuery, favorites)
        },
        combine(
            combine(_categories, _isLoadingInitial, _isLoadingMore, _isLoadingCategory) { c, i, m, lc ->
                Triple(c, Triple(i, m, lc), Unit)
            },
            combine(_error, _total, _userZipcode, _userAddress) { e, t, z, a ->
                Quadruple(e, t, z, a)
            }
        ) { loadingCats, errTotal ->
            val (categories, loads, _) = loadingCats
            val (isLoadingInitial, isLoadingMore, isLoadingCategory) = loads
            MetaSlice(
                categories = categories,
                isLoadingInitial = isLoadingInitial,
                isLoadingMore = isLoadingMore,
                isLoadingCategory = isLoadingCategory,
                error = errTotal.first,
                total = errTotal.second,
                userZipcode = errTotal.third,
                userAddress = errTotal.fourth
            )
        }
    ) { products, meta ->
        val activeProducts = when {
            products.selectedCategory == "__FAVORITES__" ->
                products.catalog.filter { products.favorites.contains(it.id) }
            products.selectedCategory != null ->
                products.categoryProducts
            else ->
                products.catalog
        }
        val filtered = filterProducts(activeProducts, products.searchQuery)

        if (meta.isLoadingInitial && products.catalog.isEmpty() && meta.error == null) {
            ProductsUiState.Loading
        } else if (meta.error != null && products.catalog.isEmpty() && products.selectedCategory == null) {
            ProductsUiState.Error(meta.error)
        } else {
            val hasMore = products.selectedCategory == null && activeProducts.size < meta.total
            ProductsUiState.Success(
                products = activeProducts,
                filteredProducts = filtered,
                categories = meta.categories,
                selectedCategory = products.selectedCategory,
                searchQuery = products.searchQuery,
                favoriteProductIds = products.favorites,
                total = if (products.selectedCategory == null) meta.total else filtered.size,
                skip = _skip.value,
                limit = _limit,
                isLoadingMore = meta.isLoadingMore,
                hasMorePages = hasMore,
                userZipcode = meta.userZipcode,
                userAddress = meta.userAddress
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductsUiState.Loading
    )

    init {
        loadCategories()
        observeLocalProducts()
        syncRemoteProducts()
        loadUserProfile()

        val savedCat = _selectedCategory.value
        if (savedCat != null && savedCat != "__FAVORITES__") {
            loadCategoryProducts(savedCat)
        }
    }

    private fun observeLocalProducts() {
        viewModelScope.launch {
            productRepository.getProducts().collect { localProducts ->
                if (localProducts.isNotEmpty() && _allProducts.value.isEmpty()) {
                    _allProducts.value = localProducts
                    _total.value = localProducts.size
                    _isLoadingInitial.value = false
                    syncCategoriesFromProducts()
                }
            }
        }
    }

    private fun syncRemoteProducts() {
        viewModelScope.launch {
            val result = productRepository.fetchProductsPage(limit = _limit, skip = 0)
            result.onSuccess { pageResult ->
                _allProducts.value = pageResult.products
                _total.value = pageResult.total
                _skip.value = pageResult.skip
                _isLoadingInitial.value = false
                syncCategoriesFromProducts()
            }.onFailure { exception ->
                if (_allProducts.value.isEmpty()) {
                    _error.value = exception.message ?: "Error al cargar los productos."
                    _isLoadingInitial.value = false
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val remote = getCategoriesUseCase()
                if (remote.isNotEmpty()) {
                    _categories.value = remote
                } else {
                    syncCategoriesFromProducts()
                }
            } catch (_: Exception) {
                syncCategoriesFromProducts()
            }
        }
    }

    private fun syncCategoriesFromProducts() {
        if (_categories.value.isNotEmpty()) return
        val fromProducts = _allProducts.value
            .map { it.category.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
            .sorted()
        if (fromProducts.isNotEmpty()) {
            _categories.value = fromProducts
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val result = getUserProfileUseCase(userId = 1)
            result.onSuccess { user ->
                if (user.zipcode.isNotBlank()) {
                    _userZipcode.value = user.zipcode
                }
                if (user.fullAddress.isNotBlank()) {
                    _userAddress.value = user.fullAddress
                }
            }
        }
    }

    fun loadInitialProducts() {
        syncRemoteProducts()
    }

    fun loadMoreProducts() {
        if (_selectedCategory.value != null) return
        val currentProducts = _allProducts.value
        if (_isLoadingMore.value || currentProducts.size >= _total.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextSkip = currentProducts.size
            val result = productRepository.fetchProductsPage(limit = _limit, skip = nextSkip)

            result.onSuccess { pageResult ->
                val newProducts = (currentProducts + pageResult.products).distinctBy { it.id }
                _allProducts.value = newProducts
                _total.value = pageResult.total
                _skip.value = pageResult.skip
                _isLoadingMore.value = false
            }.onFailure {
                _isLoadingMore.value = false
            }
        }
    }

    fun loadData() {
        val selected = _selectedCategory.value
        if (selected != null && selected != "__FAVORITES__") {
            loadCategoryProducts(selected)
        } else {
            loadInitialProducts()
        }
    }

    fun selectCategory(category: String?) {
        val current = _selectedCategory.value
        val next = if (current == category) null else category
        savedStateHandle["selected_category"] = next

        when {
            next == null || next == "__FAVORITES__" -> {
                _categoryProducts.value = emptyList()
                _isLoadingCategory.value = false
            }
            else -> loadCategoryProducts(next)
        }
    }

    private fun loadCategoryProducts(category: String) {
        viewModelScope.launch {
            _isLoadingCategory.value = true
            _error.value = null

            val result = productRepository.fetchProductsByCategory(category)
            result.onSuccess { pageResult ->
                _categoryProducts.value = pageResult.products
                _isLoadingCategory.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Error al cargar la categoría."
                _isLoadingCategory.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && query.length >= 2) {
            viewModelScope.launch {
                saveSearchHistoryUseCase(query.trim())
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase()
        }
    }

    fun deleteSearchHistory(id: String) {
        viewModelScope.launch {
            deleteSearchHistoryUseCase(id)
        }
    }

    fun toggleFavorite(productId: Int) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(productId)
        }
    }

    private fun filterProducts(products: List<Product>, query: String): List<Product> {
        if (query.isBlank()) return products
        return products.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }
}
