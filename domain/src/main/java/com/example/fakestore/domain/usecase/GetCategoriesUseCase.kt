package com.example.fakestore.domain.usecase

import com.example.fakestore.domain.repository.ProductRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): List<String> =
        productRepository.getCategories()
}
