package com.luislazarodev.androidcodechallenge.data.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val stock: Int,
    val availabilityStatus: String,
    val thumbnail: String,
    val images: List<String>,
    val score: Double
)
