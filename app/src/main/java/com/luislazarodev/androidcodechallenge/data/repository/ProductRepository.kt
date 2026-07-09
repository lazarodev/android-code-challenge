package com.luislazarodev.androidcodechallenge.data.repository

import com.luislazarodev.androidcodechallenge.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProductsFlow(): Flow<List<Product>>
    suspend fun getProductById(id: Int): Product?
    suspend fun fetchProducts(): Result<Unit>
}
