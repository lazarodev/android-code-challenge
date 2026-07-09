package com.luislazarodev.androidcodechallenge.di

import com.google.gson.Gson
import com.luislazarodev.androidcodechallenge.data.repository.ProductRepository
import com.luislazarodev.androidcodechallenge.utils.NetworkConnectivityTracker

interface AppContainerInterface {
    val productRepository: ProductRepository
    val networkConnectivityTracker: NetworkConnectivityTracker
    val gson: Gson
}
