package com.example.fakestore.core.network

data class ProductDto(
    val id: Int,
    val title: String? = null,
    val description: String? = null,
    val price: Double? = 0.0,
    val discountPercentage: Double? = 0.0,
    val rating: Double? = 0.0,
    val stock: Int? = 0,
    val brand: String? = null,
    val category: String? = null,
    val thumbnail: String? = null,
    val images: List<String>? = null,
    val tags: List<String>? = null,
    val sku: String? = null,
    val weight: Double? = null,
    val dimensions: DimensionsDto? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val availabilityStatus: String? = null,
    val reviews: List<ReviewDto>? = null,
    val returnPolicy: String? = null,
    val minimumOrderQuantity: Int? = null,
    val meta: MetaDto? = null
)

data class DimensionsDto(
    val width: Double? = 0.0,
    val height: Double? = 0.0,
    val depth: Double? = 0.0
)

data class ReviewDto(
    val rating: Double? = 0.0,
    val comment: String? = null,
    val date: String? = null,
    val reviewerName: String? = null,
    val reviewerEmail: String? = null
)

data class MetaDto(
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val barcode: String? = null,
    val qrCode: String? = null
)
