package com.luislazarodev.androidcodechallenge.data.remote

import com.luislazarodev.androidcodechallenge.data.model.ProductListResponse
import retrofit2.http.GET

interface ProductApi {
    @GET("products")
    suspend fun getProducts(): ProductListResponse
}
