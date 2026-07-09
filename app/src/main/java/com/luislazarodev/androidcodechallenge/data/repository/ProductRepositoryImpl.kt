package com.luislazarodev.androidcodechallenge.data.repository

import com.google.gson.Gson
import com.luislazarodev.androidcodechallenge.data.local.ProductDao
import com.luislazarodev.androidcodechallenge.data.local.ProductEntity
import com.luislazarodev.androidcodechallenge.data.model.Product
import com.luislazarodev.androidcodechallenge.data.remote.ProductApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class ProductRepositoryImpl(
    private val productApi: ProductApi,
    private val productDao: ProductDao,
    private val gson: Gson,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): ProductRepository {
    override fun getProductsFlow(): Flow<List<Product>> {
        return productDao.getProductsFlow().map { entities ->
            entities.map { it.toDomain(gson) }
        }
    }

    override suspend fun getProductById(id: Int): Product? = withContext(ioDispatcher) {
        productDao.getProductById(id)?.toDomain(gson)
    }

    override suspend fun fetchProducts(): Result<Unit>  = withContext(ioDispatcher) {
        try {
            val response = productApi.getProducts()
            val dtos = response.products ?: emptyList()

            val domainProducts = dtos.map { it.toDomain() }
            val entities = domainProducts.map { ProductEntity.fromDomain(it, gson) }

            productDao.clearAndInsertProducts(entities)
            Result.success(Unit)
        } catch (e: IOException) {
            Result.failure(NetworkException("Network error occurred. Please check connection.", e))
        } catch (e: retrofit2.HttpException) {
            Result.failure(ServerException("Server error with status code: ${e.code()}", e.code(), e))
        } catch (e: Exception) {
            Result.failure(UnknownException("An unexpected error occurred", e))
        }
    }

}

open class RepositoryException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NetworkException(message: String, cause: Throwable? = null) : RepositoryException(message, cause)
class ServerException(message: String, val code: Int, cause: Throwable? = null) : RepositoryException(message, cause)
class UnknownException(message: String, cause: Throwable? = null) : RepositoryException(message, cause)
