package com.example.fakestore.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fakestore.domain.model.Dimensions
import com.example.fakestore.domain.model.Product
import com.example.fakestore.domain.model.ProductMeta
import com.example.fakestore.domain.model.Rating

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val ratingRate: Double,
    val ratingCount: Int,
    val discountPercentage: Double = 0.0,
    val stock: Int = 0,
    val brand: String = "",
    val sku: String = "",
    val weight: Double = 0.0,
    val dimWidth: Double = 0.0,
    val dimHeight: Double = 0.0,
    val dimDepth: Double = 0.0,
    val warrantyInformation: String = "",
    val shippingInformation: String = "",
    val returnPolicy: String = "",
    val availabilityStatus: String = "",
    val minimumOrderQuantity: Int = 1,
    val barcode: String = "",
    val qrCode: String = ""
) {
    fun toDomain(): Product = Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = Rating(rate = ratingRate, count = ratingCount),
        discountPercentage = discountPercentage,
        stock = stock,
        brand = brand,
        images = listOf(image),
        sku = sku,
        weight = weight,
        dimensions = Dimensions(width = dimWidth, height = dimHeight, depth = dimDepth),
        warrantyInformation = warrantyInformation,
        shippingInformation = shippingInformation,
        returnPolicy = returnPolicy,
        availabilityStatus = availabilityStatus,
        minimumOrderQuantity = minimumOrderQuantity,
        meta = ProductMeta(barcode = barcode, qrCode = qrCode)
    )

    companion object {
        fun fromDomain(product: Product): ProductEntity = ProductEntity(
            id = product.id,
            title = product.title,
            price = product.price,
            description = product.description,
            category = product.category,
            image = product.image,
            ratingRate = product.rating.rate,
            ratingCount = product.rating.count,
            discountPercentage = product.discountPercentage,
            stock = product.stock,
            brand = product.brand,
            sku = product.sku,
            weight = product.weight,
            dimWidth = product.dimensions.width,
            dimHeight = product.dimensions.height,
            dimDepth = product.dimensions.depth,
            warrantyInformation = product.warrantyInformation,
            shippingInformation = product.shippingInformation,
            returnPolicy = product.returnPolicy,
            availabilityStatus = product.availabilityStatus,
            minimumOrderQuantity = product.minimumOrderQuantity,
            barcode = product.meta.barcode,
            qrCode = product.meta.qrCode
        )
    }
}
