package com.luislazarodev.androidcodechallenge.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luislazarodev.androidcodechallenge.data.repository.ProductRepository
import com.luislazarodev.androidcodechallenge.utils.NetworkConnectivityTracker
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val productRepository: ProductRepository,
    private val connectivityTracker: NetworkConnectivityTracker
) : ViewModel() {
    private val _state = MutableStateFlow(ProductListContract.State())
    val state: StateFlow<ProductListContract.State> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProductListContract.Effect>()
    val effect: SharedFlow<ProductListContract.Effect> = _effect.asSharedFlow()

    init {
        viewModelScope.launch {
            connectivityTracker.isConnectedFlow.collect { isConnected ->
                _state.update { it.copy(isOffline = !isConnected) }
                if (isConnected && _state.value.products.isEmpty()) {
                    handleIntent(ProductListContract.Intent.LoadProducts)
                }
            }
        }

        viewModelScope.launch {
            productRepository.getProductsFlow().collect { products ->
                _state.update { it.copy(products = products) }
            }
        }
    }

    fun handleIntent(intent: ProductListContract.Intent) {
        when (intent) {
            is ProductListContract.Intent.LoadProducts -> {
                if (_state.value.products.isEmpty()) {
                    fetchProducts()
                }
            }

            is ProductListContract.Intent.RefreshProducts -> {
                fetchProducts()
            }

            is ProductListContract.Intent.RetryFetch -> {
                fetchProducts()
            }
        }
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = productRepository.fetchProducts()
            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                },
                onFailure = { throwable ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "An unexpected error occurred"
                        )
                    }
                    _effect.emit(
                        ProductListContract.Effect.ShowToast(
                            throwable.message ?: "Sync failed"
                        )
                    )
                }
            )
        }
    }

    class Factory(
        private val repository: ProductRepository,
        private val connectivityTracker: NetworkConnectivityTracker
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProductListViewModel(repository, connectivityTracker) as T
        }
    }
}
