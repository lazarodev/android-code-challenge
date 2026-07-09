package com.luislazarodev.androidcodechallenge.data.local

import androidx.room.Database

@Database(entities = [ProductEntity::class], version = 1, exportSchema = false)
abstract class ProductDatabase {
    abstract fun productDao(): ProductDao
}
