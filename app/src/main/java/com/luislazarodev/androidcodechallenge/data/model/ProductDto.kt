package com.luislazarodev.androidcodechallenge.data.model

import com.google.gson.annotations.SerializedName
import com.luislazarodev.androidcodechallenge.utils.toDoubleOrZero
import com.luislazarodev.androidcodechallenge.utils.toIntOrZero

data class ProductDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Any?,
    @SerializedName("rating") val rating: Any?,
    @SerializedName("stock") val stock: Any?,
    @SerializedName("availabilityStatus") val availabilityStatus: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("images") val images: List<String>?
) {
    fun toDomain(): Product {
        val parsedPrice = price.toDoubleOrZero()
        val parsedRating = rating.toDoubleOrZero()
        val parsedStock = stock.toIntOrZero()

        return Product(
            id = id ?: 0,
            title = title.orEmpty(),
            description = description.orEmpty(),
            price = parsedPrice,
            rating = parsedRating,
            stock = parsedStock,
            availabilityStatus = availabilityStatus ?: "Unknown",
            thumbnail = thumbnail.orEmpty(),
            images = images ?: emptyList(),
            score = 0.0 // TODO: Calculate score
        )
    }
}
