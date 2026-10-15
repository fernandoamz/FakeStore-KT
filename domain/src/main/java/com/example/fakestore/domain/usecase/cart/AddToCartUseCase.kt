package com.example.fakestore.domain.usecase.cart

import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(item: CartItem) {
        cartRepository.addProduct(item)
    }
}
