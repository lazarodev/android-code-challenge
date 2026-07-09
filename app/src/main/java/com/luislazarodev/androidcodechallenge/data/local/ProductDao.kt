package com.luislazarodev.androidcodechallenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Adding order by score filter as per requirement
    @Query("SELECT * FROM products ORDER BY score DESC")
    fun getProductsFlow(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Int): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<ProductEntity>): List<Long>

    @Query("DELETE FROM products")
    fun clearProducts(): Int

    @Transaction
    fun clearAndInsertProducts(products: List<ProductEntity>): List<Long> {
        clearProducts()
        return insertProducts(products)
    }
}
