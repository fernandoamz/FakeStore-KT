package com.example.fakestore.feature.products.address.presentation

data class AddressUiState(
    val postalCode: String = "",
    val state: String = "",
    val city: String = "",
    val colony: String = "",
    val street: String = "",
    val exteriorNumber: String = "",
    val interiorNumber: String = "",
    val references: String = "",
    val recipientName: String = "",
    val phoneNumber: String = "",
    val isPostalCodeValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)
