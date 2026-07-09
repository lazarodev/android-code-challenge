package com.luislazarodev.androidcodechallenge.data.model

import kotlin.math.ln
import kotlin.math.max

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
) {
    companion object {
        fun calculateScore(rating: Double?, stock: Int?, price: Double?): Double {
            val r = rating?.takeIf { it >= 0 } ?: 0.0
            val s = stock?.takeIf { it >= 0 } ?: 0
            val p = price?.takeIf { it >= 0 } ?: 0.0

            val numerator = r * ln(s.toDouble() + 1.0)
            val denominator = max(p, 1.0)
            return numerator / denominator
        }
    }
}
