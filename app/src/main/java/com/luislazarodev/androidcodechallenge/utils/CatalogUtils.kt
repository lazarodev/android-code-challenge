package com.luislazarodev.androidcodechallenge.utils

// Data normalization helper extensions
fun Any?.toDoubleOrZero(): Double {
    if (this == null) return 0.0
    if (this is Number) return this.toDouble()
    if (this is String) {
        if (this.isBlank()) return 0.0
        return this.toDoubleOrNull() ?: 0.0
    }
    return 0.0
}

fun Any?.toIntOrZero(): Int {
    if (this == null) return 0
    if (this is Number) return this.toInt()
    if (this is String) {
        if (this.isBlank()) return 0
        return this.toIntOrNull() ?: 0
    }
    return 0
}
