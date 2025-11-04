package com.example.pasteleriamilsabores.ui.screens.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pasteleriamilsabores.data.model.Product
import com.example.pasteleriamilsabores.ui.screens.login.AuthViewModel
import com.example.pasteleriamilsabores.ui.screens.login.AuthViewModelFactory
import com.example.pasteleriamilsabores.ui.screens.products.ProductViewModel
import com.example.pasteleriamilsabores.data.model.formatPrice
import com.example.pasteleriamilsabores.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, productViewModel: ProductViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val cartItems by productViewModel.cart.observeAsState(initial = emptyList())
    val cartItemCount = cartItems.sumOf { it.quantity }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(navController, scope, drawerState)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pastelería Mil Sabores") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
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
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            HomeScreenContent(paddingValues, productViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(navController: NavController, scope: kotlinx.coroutines.CoroutineScope, drawerState: DrawerState) {
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current.applicationContext as Application))
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Mil Sabores",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Catálogo") },
            label = { Text("Catálogo de Productos") },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Screen.ProductList.route)
                }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
            label = { Text("Carrito de Compras") },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    navController.navigate(Screen.Cart.route)
                }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = if (isLoggedIn) "Mi Perfil" else "Login") },
            label = { Text(if (isLoggedIn) "Mi Perfil" else "Iniciar Sesión") },
            selected = false,
            onClick = {
                scope.launch {
                    drawerState.close()
                    if (isLoggedIn) {
                        navController.navigate(Screen.Profile.route)
                    } else {
                        navController.navigate(Screen.Login.route)
                    }
                }
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
fun HomeScreenContent(paddingValues: PaddingValues, productViewModel: ProductViewModel) {
    val featuredProducts by productViewModel.products.observeAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Productos Destacados",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(featuredProducts.take(3)) { product ->
                FeaturedProductItem(product = product, onAddToCart = { productViewModel.addToCart(product) })
            }
        }
    }
}

@Composable
fun FeaturedProductItem(product: Product, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                formatPrice(product.price),
                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.ExtraBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "AGREGAR",
                    style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.onPrimary)
                )
            }
        }
    }
}
