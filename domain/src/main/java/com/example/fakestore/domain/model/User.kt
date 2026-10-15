package com.example.fakestore.domain.model

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val street: String,
    val number: Int,
    val city: String,
    val zipcode: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()

    val fullAddress: String
        get() = "$street $number, $city (CP: $zipcode)".trim()
}
