package com.example.fakestore.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fakestore.MainViewModel
import com.example.fakestore.domain.model.OrderResult
import com.example.fakestore.feature.products.auth.LoginScreen
import com.example.fakestore.feature.products.auth.RegisterScreen
import com.example.fakestore.feature.products.camera.CameraScreen
import com.example.fakestore.feature.products.cart.CartScreen
import com.example.fakestore.feature.products.catalog.ProductDetailScreen
import com.example.fakestore.feature.products.catalog.ProductsScreen
import com.example.fakestore.feature.products.checkout.CheckoutScreen
import com.example.fakestore.feature.products.checkout.OrderConfirmationScreen
import com.example.fakestore.feature.products.components.BottomTab
import com.example.fakestore.feature.products.llm.LLMScreen
import com.example.fakestore.feature.products.profile.ProfileScreen
import com.example.fakestore.feature.products.videos.VideosScreen

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val isLoggedInState by mainViewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (isLoggedInState == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "products"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("products") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable("products") {
            ProductsScreen(
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onTabSelected = { tab ->
                    navigateBottomTab(
                        navController = navController,
                        tab = tab,
                        currentRoute = "products",
                        isLoggedIn = isLoggedInState == true
                    )
                },
                onProfileClick = {
                    if (isLoggedInState == true) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("login")
                    }
                },
                onCameraClick = {
                    navController.navigate("camera")
                }
            )
        }

        composable("camera") {
            CameraScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("llm") {
            LLMScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onTabSelected = { tab ->
                    navigateBottomTab(
                        navController = navController,
                        tab = tab,
                        currentRoute = "llm",
                        isLoggedIn = isLoggedInState == true
                    )
                },
                onCameraClick = {
                    navController.navigate("camera")
                }
            )
        }

        composable("videos") {
            VideosScreen(
                onProductClick = { productId ->
                    navController.navigate("product/$productId")
                },
                onTabSelected = { tab ->
                    navigateBottomTab(
                        navController = navController,
                        tab = tab,
                        currentRoute = "videos",
                        isLoggedIn = isLoggedInState == true
                    )
                },
                onProfileClick = {
                    if (isLoggedInState == true) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onLogoutSuccess = {
                    navController.navigate("products") {
                        popUpTo("profile") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "product/{productId}",
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments
                ?.getInt("productId") ?: return@composable

            ProductDetailScreen(
                productId = productId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCartClick = {
                    navController.navigate("cart")
                }
            )
        }

        composable("cart") {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCheckoutClick = {
                    navController.navigate("checkout")
                },
                onTabSelected = { tab ->
                    navigateBottomTab(
                        navController = navController,
                        tab = tab,
                        currentRoute = "cart",
                        isLoggedIn = isLoggedInState == true
                    )
                }
            )
        }

        composable("checkout") {
            CheckoutScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderConfirmed = { orderResult: OrderResult ->
                    navController.navigate("order_confirmation/${orderResult.id}/${orderResult.date}/${orderResult.totalPrice.toFloat()}/${orderResult.totalItems}") {
                        popUpTo("cart") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "order_confirmation/{orderId}/{date}/{totalPrice}/{totalItems}",
            arguments = listOf(
                navArgument("orderId") { type = NavType.IntType },
                navArgument("date") { type = NavType.StringType },
                navArgument("totalPrice") { type = NavType.FloatType },
                navArgument("totalItems") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            val date = backStackEntry.arguments?.getString("date").orEmpty()
            val totalPrice = (backStackEntry.arguments?.getFloat("totalPrice") ?: 0f).toDouble()
            val totalItems = backStackEntry.arguments?.getInt("totalItems") ?: 0

            OrderConfirmationScreen(
                orderId = orderId,
                date = date,
                totalPrice = totalPrice,
                totalItems = totalItems,
                onBackToStoreClick = {
                    navController.navigate("products") {
                        popUpTo("products") { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun navigateBottomTab(
    navController: NavHostController,
    tab: BottomTab,
    currentRoute: String,
    isLoggedIn: Boolean
) {
    if (tab.route == currentRoute) return

    when (tab) {
        BottomTab.MORE -> {
            navController.navigate(if (isLoggedIn) "profile" else "login")
        }
        else -> {
            navController.navigate(tab.route) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}
