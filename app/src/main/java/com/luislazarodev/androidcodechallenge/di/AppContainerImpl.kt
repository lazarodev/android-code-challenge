package com.luislazarodev.androidcodechallenge.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.luislazarodev.androidcodechallenge.BuildConfig
import com.luislazarodev.androidcodechallenge.data.local.ProductDatabase
import com.luislazarodev.androidcodechallenge.data.remote.ProductApi
import com.luislazarodev.androidcodechallenge.data.repository.ProductRepository
import com.luislazarodev.androidcodechallenge.data.repository.ProductRepositoryImpl
import com.luislazarodev.androidcodechallenge.utils.NetworkConnectivityTracker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AppContainerImpl(private val context: Context) : AppContainerInterface {

    override val gson: Gson by lazy {
        Gson()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    private val database: ProductDatabase by lazy {
        Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "catalogo_express_db"
        ).build()
    }

    override val networkConnectivityTracker: NetworkConnectivityTracker by lazy {
        NetworkConnectivityTracker(context)
    }

    override val productRepository: ProductRepository by lazy {
        ProductRepositoryImpl(
            productApi = productApi,
            productDao = database.productDao(),
            gson = gson
        )
    }
}
