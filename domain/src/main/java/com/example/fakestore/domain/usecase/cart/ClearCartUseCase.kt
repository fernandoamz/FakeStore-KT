package com.example.fakestore.domain.usecase.cart

import com.example.fakestore.domain.repository.CartRepository
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke() {
        cartRepository.clearCart()
    }
}
