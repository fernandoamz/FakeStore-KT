package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetProductsByCategoryUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(category: String): Flow<List<Product>> =
        productRepository.getProductsByCategory(category)
}
