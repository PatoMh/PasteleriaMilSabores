package com.example.pasteleriamilsabores.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.ui.screens.products.ProductViewModel
import com.example.pasteleriamilsabores.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    navController: NavController,
    productViewModel: ProductViewModel,
    navigationIcon: @Composable (() -> Unit)? = null
) {
    val cartItems by productViewModel.cart.observeAsState(initial = emptyList())
    val cartItemCount = cartItems.sumOf { it.quantity }

    TopAppBar(
        title = { Text(title) },
        navigationIcon = navigationIcon ?: {},
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                BadgedBox(badge = {
                    if (cartItemCount > 0) {
                        Badge { Text(cartItemCount.toString()) }
                    }
                }) {
                    Icon(
                        Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito de Compras"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
