package com.luislazarodev.androidcodechallenge.ui.screens.detail

import com.luislazarodev.androidcodechallenge.data.model.Product

interface ProductDetailContract {
    data class State(
        val isLoading: Boolean = false,
        val product: Product? = null,
        val error: String? = null
    )

    sealed interface Intent {
        data class LoadProductDetail(val productId: Int) : Intent
    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
    }
}
