package com.example.pasteleriamilsabores.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pasteleriamilsabores.ui.screens.cart.CartScreen
import com.example.pasteleriamilsabores.ui.screens.home.HomeScreen
import com.example.pasteleriamilsabores.ui.screens.login.LoginScreen
import com.example.pasteleriamilsabores.ui.screens.products.ProductListScreen
import com.example.pasteleriamilsabores.ui.screens.products.ProductViewModel
import com.example.pasteleriamilsabores.ui.screens.profile.ProfileScreen
import com.example.pasteleriamilsabores.ui.screens.register.RegisterScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object ProductList : Screen("productList")
    object Cart : Screen("cart")
    object Profile : Screen("profile")
    object Register : Screen("register")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val productViewModel: ProductViewModel = viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(navController.context.applicationContext as android.app.Application)
    )

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.ProductList.route) {
            ProductListScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
    }
}
