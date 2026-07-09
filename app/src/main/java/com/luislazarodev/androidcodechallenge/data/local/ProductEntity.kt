package com.luislazarodev.androidcodechallenge.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luislazarodev.androidcodechallenge.data.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val stock: Int,
    val availabilityStatus: String,
    val thumbnail: String,
    val imagesJson: String,
    val score: Double
) {
    fun toDomain(gson: com.google.gson.Gson): Product {
        val imagesList: List<String> = try {
            val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
            gson.fromJson(imagesJson, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        return Product(
            id = id,
            title = title,
            description = description,
            price = price,
            rating = rating,
            stock = stock,
            availabilityStatus = availabilityStatus,
            thumbnail = thumbnail,
            images = imagesList,
            score = score
        )
    }

    companion object {
        fun fromDomain(product: Product, gson: com.google.gson.Gson): ProductEntity {
            val imagesJson = gson.toJson(product.images)
            return ProductEntity(
                id = product.id,
                title = product.title,
                description = product.description,
                price = product.price,
                rating = product.rating,
                stock = product.stock,
                availabilityStatus = product.availabilityStatus,
                thumbnail = product.thumbnail,
                imagesJson = imagesJson,
                score = product.score
            )
        }
    }
}
