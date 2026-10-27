package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.FavoritesRepository
import com.example.fakestore.domain.repository.ProductRepository
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import com.example.fakestore.domain.usecase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _catalogProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _categoryProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _selectedCategory = MutableStateFlow<String?>(null)
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

    val uiState: StateFlow<ProductsUiState> = combine(
        combine(
            _catalogProducts,
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
        val isBusy = meta.isLoadingInitial || meta.isLoadingCategory

        if (isBusy && activeProducts.isEmpty() && meta.error == null) {
            ProductsUiState.Loading
        } else if (meta.error != null && activeProducts.isEmpty() && products.selectedCategory == null) {
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
        loadInitialProducts()
        loadUserProfile()
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
        val fromProducts = _catalogProducts.value
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
        viewModelScope.launch {
            _isLoadingInitial.value = true
            _error.value = null
            _skip.value = 0

            val result = productRepository.fetchProductsPage(limit = _limit, skip = 0)
            result.onSuccess { pageResult ->
                _catalogProducts.value = pageResult.products
                _total.value = pageResult.total
                _skip.value = pageResult.skip
                _isLoadingInitial.value = false
                syncCategoriesFromProducts()
            }.onFailure { exception ->
                productRepository.getProducts().collect { cached ->
                    if (cached.isNotEmpty()) {
                        _catalogProducts.value = cached
                        _total.value = cached.size
                        _isLoadingInitial.value = false
                        syncCategoriesFromProducts()
                    } else {
                        _error.value = exception.message ?: "Error al cargar los productos."
                        _isLoadingInitial.value = false
                    }
                }
            }
        }
    }

    fun loadMoreProducts() {
        if (_selectedCategory.value != null) return
        val currentProducts = _catalogProducts.value
        if (_isLoadingMore.value || currentProducts.size >= _total.value) return

        viewModelScope.launch {
            _isLoadingMore.value = true
            val nextSkip = currentProducts.size
            val result = productRepository.fetchProductsPage(limit = _limit, skip = nextSkip)

            result.onSuccess { pageResult ->
                val newProducts = (currentProducts + pageResult.products).distinctBy { it.id }
                _catalogProducts.value = newProducts
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
        val next = if (_selectedCategory.value == category) null else category
        _selectedCategory.value = next

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
            _categoryProducts.value = emptyList()

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

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
