package com.example.pasteleriamilsabores.ui.screens.cart

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.ui.components.AppBar
import com.example.pasteleriamilsabores.data.model.CartItem
import com.example.pasteleriamilsabores.ui.screens.products.ProductViewModel
import com.example.pasteleriamilsabores.data.model.formatPrice
import com.example.pasteleriamilsabores.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController, productViewModel: ProductViewModel) {
    val cartItems by productViewModel.cart.observeAsState(initial = emptyList())
    val total = cartItems.sumOf { it.product.price * it.quantity }

    Scaffold(
        topBar = {
            AppBar(
                title = "Carrito de Compras",
                navController = navController,
                productViewModel = productViewModel,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total:", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                            Text(
                                formatPrice(total),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                productViewModel.sendSaleToBackend()
                                navController.navigate(Screen.ProductList.route) {
                                    popUpTo(Screen.ProductList.route) { inclusive = true }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("ENVIAR VENTA", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tu carrito está vacío. ¡Añade algunas delicias!",
                    style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cartItems, key = { it.product.code }) { item ->
                    CartItemView(item, productViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartItemView(cartItem: CartItem, productViewModel: ProductViewModel) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart || it == SwipeToDismissBoxValue.StartToEnd) {
                productViewModel.removeCartItem(cartItem.product.code)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFE57373)
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFE57373)
                    else -> Color.Transparent
                },
                label = ""
            )
            val scale by animateFloatAsState(
                if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) 1.2f else 0.8f,
                label = ""
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    modifier = Modifier.scale(scale),
                    tint = Color.White
                )
            }
        }
    ) { 
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(cartItem.product.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold))
                    Text(formatPrice(cartItem.product.price), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { productViewModel.updateCartItemQuantity(cartItem.product.code, cartItem.quantity - 1) }
                    ) {
                        Icon(Icons.Filled.RemoveCircle, contentDescription = "Quitar", tint = MaterialTheme.colorScheme.primary)
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { productViewModel.updateCartItemQuantity(cartItem.product.code, cartItem.quantity + 1) }
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = "Añadir", tint = MaterialTheme.colorScheme.primary)
                    }

                    IconButton(
                        onClick = { productViewModel.removeCartItem(cartItem.product.code) }
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
