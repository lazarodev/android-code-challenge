package com.luislazarodev.androidcodechallenge.ui.screens.list

import com.luislazarodev.androidcodechallenge.data.model.Product

interface ProductListContract {
    data class State(
        val isLoading: Boolean = false,
        val products: List<Product> = emptyList(),
        val isOffline: Boolean = false,
        val error: String? = null
    )

    sealed interface Intent {
        object LoadProducts : Intent
        object RefreshProducts : Intent
        object RetryFetch : Intent
    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
    }
}
