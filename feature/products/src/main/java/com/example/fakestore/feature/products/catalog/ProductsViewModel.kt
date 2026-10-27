package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.FavoritesRepository
import com.example.fakestore.domain.repository.ProductRepository
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private data class StateTuple(
    val categories: List<String>,
    val isLoadingInitial: Boolean,
    val isLoadingMore: Boolean,
    val error: String?,
    val total: Int
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val favoritesRepository: FavoritesRepository
) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    private val _total = MutableStateFlow(0)
    private val _skip = MutableStateFlow(0)
    private val _limit = 10
    private val _isLoadingInitial = MutableStateFlow(true)
    private val _isLoadingMore = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductsUiState> = combine(
        _allProducts,
        _selectedCategory,
        _searchQuery,
        favoritesRepository.favoriteProductIds
    ) { products, selectedCategory, searchQuery, favorites ->
        val activeCategoryProducts = if (selectedCategory == "__FAVORITES__") {
            products.filter { favorites.contains(it.id) }
        } else if (selectedCategory != null) {
            products.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        } else {
            products
        }
        val filtered = filterProducts(activeCategoryProducts, searchQuery)
        Triple(products, filtered, favorites)
    }.combine(
        combine(_categories, _isLoadingInitial, _isLoadingMore, _error, _total) { categories, isLoadingInitial, isLoadingMore, error, total ->
            StateTuple(categories, isLoadingInitial, isLoadingMore, error, total)
        }
    ) { (products, filtered, favorites), (categories, isLoadingInitial, isLoadingMore, error, total) ->
        if (isLoadingInitial && products.isEmpty() && error == null) {
            ProductsUiState.Loading
        } else if (error != null && products.isEmpty()) {
            ProductsUiState.Error(error)
        } else {
            val hasMore = products.size < total
            ProductsUiState.Success(
                products = products,
                filteredProducts = filtered,
                categories = categories,
                selectedCategory = _selectedCategory.value,
                searchQuery = _searchQuery.value,
                favoriteProductIds = favorites,
                total = total,
                skip = _skip.value,
                limit = _limit,
                isLoadingMore = isLoadingMore,
                hasMorePages = hasMore
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
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = getCategoriesUseCase()
            } catch (_: Exception) {
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
                _allProducts.value = pageResult.products
                _total.value = pageResult.total
                _skip.value = pageResult.skip
                _isLoadingInitial.value = false
            }.onFailure { exception ->
                productRepository.getProducts().collect { cached ->
                    if (cached.isNotEmpty()) {
                        _allProducts.value = cached
                        _total.value = cached.size
                        _isLoadingInitial.value = false
                    } else {
                        _error.value = exception.message ?: "Error al cargar los productos."
                        _isLoadingInitial.value = false
                    }
                }
            }
        }
    }

    fun loadMoreProducts() {
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
        loadInitialProducts()
    }

    fun selectCategory(category: String?) {
        val current = _selectedCategory.value
        _selectedCategory.value = if (current == category) null else category
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
