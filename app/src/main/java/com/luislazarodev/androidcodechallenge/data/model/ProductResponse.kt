package com.luislazarodev.androidcodechallenge.data.model

import com.google.gson.annotations.SerializedName

data class ProductListResponse(
    @SerializedName("products") val products: List<ProductDto>?,
    @SerializedName("total") val total: Int?,
    @SerializedName("skip") val skip: Int?,
    @SerializedName("limit") val limit: Int?
)
