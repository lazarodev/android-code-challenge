package com.luislazarodev.androidcodechallenge

import android.app.Application
import com.luislazarodev.androidcodechallenge.di.AppContainerImpl
import com.luislazarodev.androidcodechallenge.di.AppContainerInterface

class CatalogoExpressApplication: Application() {
    lateinit var container: AppContainerInterface

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
