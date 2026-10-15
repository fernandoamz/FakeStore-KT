package com.example.fakestore.feature.products.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.usecase.GetCategoriesUseCase
import com.example.fakestore.domain.usecase.GetProductsByCategoryUseCase
import com.example.fakestore.domain.usecase.GetProductsUseCase
import com.example.fakestore.domain.usecase.RefreshProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val refreshProductsUseCase: RefreshProductsUseCase
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    private val _isRefreshing = MutableStateFlow(false)
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    private val _favoriteProductIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ProductsUiState> = combine(
        _selectedCategory.flatMapLatest { category ->
            if (category == null) {
                getProductsUseCase()
            } else {
                getProductsByCategoryUseCase(category)
            }
        },
        _selectedCategory,
        _searchQuery,
        _isRefreshing
    ) { products, selectedCategory, searchQuery, isRefreshing ->
        val filtered = filterProducts(products, searchQuery)
        Triple(products, filtered, isRefreshing)
    }.combine(
        combine(_categories, _favoriteProductIds, _error) { cats, favs, err ->
            Triple(cats, favs, err)
        }
    ) { (products, filtered, isRefreshing), (categories, favorites, error) ->
        ProductsUiState(
            isLoading = products.isEmpty() && isRefreshing,
            products = products,
            filteredProducts = filtered,
            categories = categories,
            selectedCategory = _selectedCategory.value,
            searchQuery = _searchQuery.value,
            favoriteProductIds = favorites,
            isRefreshing = isRefreshing,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductsUiState(isLoading = true)
    )

    init {
        loadCategories()
        refresh()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = getCategoriesUseCase()
            } catch (_: Exception) {
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            val result = refreshProductsUseCase()
            result.onFailure { exception ->
                _error.value = exception.message ?: "No se pudo actualizar. Mostrando datos guardados."
            }
            _isRefreshing.value = false
        }
    }

    fun loadData() {
        refresh()
    }

    fun selectCategory(category: String?) {
        val current = _selectedCategory.value
        _selectedCategory.value = if (current == category) null else category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(productId: Int) {
        _favoriteProductIds.update { favorites ->
            val mutable = favorites.toMutableSet()
            if (mutable.contains(productId)) {
                mutable.remove(productId)
            } else {
                mutable.add(productId)
            }
            mutable
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
