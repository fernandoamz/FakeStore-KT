package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject

class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(query: String): List<Product> {
        if (query.isBlank()) return emptyList()
        return productRepository.searchProducts(query.trim())
    }
}
