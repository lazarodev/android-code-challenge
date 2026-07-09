package com.luislazarodev.androidcodechallenge.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luislazarodev.androidcodechallenge.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ProductDetailContract.State())
    val state: StateFlow<ProductDetailContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProductDetailContract.Effect>()
    val effect: SharedFlow<ProductDetailContract.Effect> = _effect.asSharedFlow()

    fun handleIntent(intent: ProductDetailContract.Intent) {
        when (intent) {
            is ProductDetailContract.Intent.LoadProductDetail -> {
                loadProduct(intent.productId)
            }
        }
    }

    private fun loadProduct(productId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val product = productRepository.getProductById(productId)
            if (product != null) {
                _state.update { it.copy(isLoading = false, product = product) }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Producto no encontrado en el catálogo"
                    )
                }
                _effect.emit(ProductDetailContract.Effect.ShowToast("No se pudo cargar el producto"))
            }
        }
    }

    class Factory(
        private val repository: ProductRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductDetailViewModel(repository) as T
        }
    }
}
