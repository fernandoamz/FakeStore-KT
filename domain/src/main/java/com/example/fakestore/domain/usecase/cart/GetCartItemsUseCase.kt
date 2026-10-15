package com.example.fakestore.domain.usecase.cart

import com.example.fakestore.domain.model.CartItem
import com.example.fakestore.domain.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): StateFlow<List<CartItem>> =
        cartRepository.cartItems
}
