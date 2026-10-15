package com.example.fakestore.domain.usecase.cart

import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.domain.repository.CartRepository
import javax.inject.Inject

class CheckoutCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(): Result<OrderResult> =
        cartRepository.checkout()
}
