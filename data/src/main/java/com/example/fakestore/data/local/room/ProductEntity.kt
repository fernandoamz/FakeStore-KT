package com.example.fakestore.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fakestore.domain.model.Product
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
    val ratingCount: Int
) {
    fun toDomain(): Product = Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = Rating(rate = ratingRate, count = ratingCount)
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
            ratingCount = product.rating.count
        )
    }
}
