package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(id: Int): Product {
        require(id > 0) {
            "Invalid product id"
        }

        return productRepository.getProductById(id)
    }
}
