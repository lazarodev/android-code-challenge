package com.luislazarodev.androidcodechallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luislazarodev.androidcodechallenge.ui.screens.detail.ProductDetailScreen
import com.luislazarodev.androidcodechallenge.ui.screens.detail.ProductDetailViewModel
import com.luislazarodev.androidcodechallenge.ui.screens.list.ProductListContract
import com.luislazarodev.androidcodechallenge.ui.screens.list.ProductListScreen
import com.luislazarodev.androidcodechallenge.ui.screens.list.ProductListViewModel
import com.luislazarodev.androidcodechallenge.ui.theme.AndroidCodeChallengeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as CatalogoExpressApplication).container

        setContent {
            AndroidCodeChallengeTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "list"
                ) {
                    composable("list") {
                        val listViewModel: ProductListViewModel = viewModel(
                            factory = ProductListViewModel.Factory(
                                repository = appContainer.productRepository,
                                connectivityTracker = appContainer.networkConnectivityTracker
                            )
                        )

                        LaunchedEffect(Unit) {
                            listViewModel.handleIntent(ProductListContract.Intent.LoadProducts)
                        }

                        ProductListScreen(
                            viewModel = listViewModel,
                            onProductClick = { product ->
                                navController.navigate("detail/${product.id}")
                            }
                        )
                    }

                    composable(
                        route = "detail/{productId}",
                        arguments = listOf(
                            navArgument("productId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        val detailViewModel: ProductDetailViewModel = viewModel(
                            factory = ProductDetailViewModel.Factory(
                                repository = appContainer.productRepository
                            )
                        )

                        ProductDetailScreen(
                            productId = productId,
                            viewModel = detailViewModel,
                            onBackClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
