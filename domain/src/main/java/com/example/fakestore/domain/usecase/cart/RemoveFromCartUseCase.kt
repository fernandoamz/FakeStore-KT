package com.example.fakestore.domain.usecase.cart

import com.example.fakestore.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(productId: Int) {
        cartRepository.removeProduct(productId)
    }
}
