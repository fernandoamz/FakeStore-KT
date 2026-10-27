package com.example.fakestore.domain.model

data class Rating(
    val rate: Double = 0.0,
    val count: Int = 0
)

data class Dimensions(
    val width: Double = 0.0,
    val height: Double = 0.0,
    val depth: Double = 0.0
) {
    val formatted: String
        get() = "${width}x${height}x${depth} cm"
}

data class Review(
    val rating: Double = 0.0,
    val comment: String = "",
    val date: String = "",
    val reviewerName: String = "",
    val reviewerEmail: String = ""
)

data class ProductMeta(
    val createdAt: String = "",
    val updatedAt: String = "",
    val barcode: String = "",
    val qrCode: String = ""
)

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating = Rating(),
    val discountPercentage: Double = 0.0,
    val stock: Int = 0,
    val brand: String = "",
    val images: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val sku: String = "",
    val weight: Double = 0.0,
    val dimensions: Dimensions = Dimensions(),
    val warrantyInformation: String = "",
    val shippingInformation: String = "",
    val returnPolicy: String = "",
    val availabilityStatus: String = "",
    val reviews: List<Review> = emptyList(),
    val minimumOrderQuantity: Int = 1,
    val meta: ProductMeta = ProductMeta()
) {
    val originalPrice: Double
        get() = if (discountPercentage > 0) price / (1 - (discountPercentage / 100)) else price

    val galleryImages: List<String>
        get() = if (images.isNotEmpty()) images else listOf(image)
}
