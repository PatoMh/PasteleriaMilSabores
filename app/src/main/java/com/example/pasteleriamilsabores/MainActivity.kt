package com.example.pasteleriamilsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
// El import de fontResource es necesario aunque comentemos las fuentes
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.text.TextStyle // Importado explícitamente
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.text.NumberFormat
import java.util.Locale

// -------------------------------------------------------------------
// 1. MODELOS DE DATOS (DATA MODELS)
// -------------------------------------------------------------------

data class Product(
    val code: String,
    val category: String,
    val name: String,
    val price: Int,
    val description: String,
    val imageUrl: String // URL o referencia a imagen simulada
)

data class CartItem(
    val product: Product,
    var quantity: Int
)

// -------------------------------------------------------------------
// 2. VIEW MODELS
// -------------------------------------------------------------------

class AuthViewModel : ViewModel() {
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    fun login(username: String, password: String) {
        // SIMULACIÓN DE AUTENTICACIÓN
        if (username == "pasteleria" && password == "mil1000") {
            _isLoggedIn.value = true
        } else {
            _isLoggedIn.value = false
        }
    }
}

class ProductViewModel : ViewModel() {
    // Simulación de productos (DB)
    private val sampleProducts = listOf(
        Product("TC001", "Tortas Cuadradas", "Torta Cuadrada de Chocolate", 45000, "Deliciosa torta de chocolate con ganache.", "placeholder_cake1"),
        Product("TC002", "Tortas Cuadradas", "Torta Cuadrada de Frutas", 50000, "Mezcla de frutas frescas y crema chantilly.", "placeholder_cake2"),
        Product("TT001", "Tortas Circulares", "Torta Circular de Vainilla", 40000, "Bizcocho clásico con crema pastelera.", "placeholder_cake3"),
        Product("TT002", "Tortas Circulares", "Torta Circular de Manjar", 42000, "Torta tradicional chilena con manjar y nueces.", "placeholder_cake4"),
        Product("P1001", "Postres Individuales", "Mousse de Chocolate", 5000, "Postre individual cremoso y suave.", "placeholder_mousse"),
        Product("PSA001", "Productos Sin Azúcar", "Torta Sin Azúcar de Naranja", 48000, "Torta ligera y deliciosa, endulzada naturalmente.", "placeholder_sugarfree"),
    )

    // LiveData para el carrito de compras
    private val _cart = MutableLiveData<List<CartItem>>(emptyList())
    val cart: LiveData<List<CartItem>> = _cart

    // LiveData para la búsqueda (filtro)
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    private val _filteredProducts = MutableLiveData(sampleProducts)
    // Exponemos los productos filtrados
    val products: LiveData<List<Product>> = _filteredProducts


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Implementación de filtro simple en el ViewModel
        _filteredProducts.value = sampleProducts.filter {
            it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
        }
    }

    fun addToCart(product: Product) {
        val currentCart = _cart.value.orEmpty().toMutableList()
        val existingItem = currentCart.find { it.product.code == product.code }

        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _cart.value = currentCart
    }

    fun updateCartItemQuantity(productCode: String, quantity: Int) {
        val currentCart = _cart.value.orEmpty().toMutableList()
        val itemIndex = currentCart.indexOfFirst { it.product.code == productCode }

        if (itemIndex != -1) {
            if (quantity <= 0) {
                currentCart.removeAt(itemIndex) // Eliminar si la cantidad es cero o menos
            } else {
                currentCart[itemIndex].quantity = quantity
            }
            _cart.value = currentCart
        }
    }

    fun removeCartItem(productCode: String) {
        _cart.value = _cart.value.orEmpty().filter { it.product.code != productCode }
    }

    fun sendSaleToBackend() {
        if (_cart.value.isNullOrEmpty()) {
            println("El carrito está vacío. Venta no enviada.")
            return
        }
        val total = _cart.value.orEmpty().sumOf { it.product.price * it.quantity }
        println("--- Venta Enviada al Backend ---")
        println("Productos en el Carrito: ${_cart.value?.size}")
        println("Total de la Venta (CLP): $${formatPrice(total)}")
        println("Simulación: Se registraría la venta en la Base de Datos.")

        // Limpiar carrito después de simular la venta
        _cart.value = emptyList()
    }
}

// -------------------------------------------------------------------
// 3. NAVEGACIÓN (SCREENS)
// -------------------------------------------------------------------

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ProductList : Screen("productList")
    object Cart : Screen("cart")
}

// -------------------------------------------------------------------
// 4. THEME Y ESTILOS
// -------------------------------------------------------------------
// Definición de colores como constantes de Color
val CreamPastel = Color(0xFFFFF5E1) // Color de Fondo Principal
val PinkSoft = Color(0xFFFFC0CB)   // Rosa Suave
val Chocolate = Color(0xFF8B4513)   // Chocolate
val DarkBrown = Color(0xFF5D4037)   // Marrón Oscuro (Texto Principal)
val LightGrey = Color(0xFFB0BEC5)   // Gris Claro (Texto Secundario)

// --- FUENTES TEMPORALMENTE COMENTADAS PARA FORZAR COMPILACIÓN DE R ---
// val Pacifico: FontFamily
//     @Composable
//     get() = FontFamily(Font(fontResource(R.font.pacifico_regular).typeface))

// val Lato: FontFamily
//     @Composable
//     get() = FontFamily(Font(fontResource(R.font.lato_regular).typeface))

// --- FUENTES DE RESPALDO (Usando el sistema por defecto hasta que se resuelva R) ---
val Pacifico = FontFamily.Cursive
val Lato = FontFamily.SansSerif
// -----------------------------------------------------------------------------

@Composable
fun PasteleriaTheme(content: @Composable () -> Unit) {
    val colors = lightColors(
        primary = PinkSoft,
        primaryVariant = Chocolate,
        secondary = PinkSoft,
        background = CreamPastel,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = DarkBrown,
        onBackground = DarkBrown,
        onSurface = DarkBrown
    )

    // Usamos las variables Composable de FontFamily
    val typography = Typography(
        // Usamos las fuentes de respaldo
        defaultFontFamily = Lato,
        h4 = TextStyle(fontFamily = Pacifico, fontSize = 30.sp, color = DarkBrown),
        h6 = TextStyle(fontFamily = Pacifico, fontSize = 20.sp, color = DarkBrown),
        body1 = TextStyle(fontFamily = Lato, fontSize = 16.sp, color = DarkBrown),
        button = TextStyle(fontFamily = Lato, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    )

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = Shapes(
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}

// Función auxiliar para formato de precio CLP (Chilean Peso)
fun formatPrice(price: Int): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    format.maximumFractionDigits = 0
    return format.format(price)
}

// -------------------------------------------------------------------
// 5. COMPONENTES DE UI (PANTALLAS)
// -------------------------------------------------------------------

@Composable
fun AppBar(navController: NavController, productViewModel: ProductViewModel) {
    // observeAsState está aquí
    val cartCount by productViewModel.cart.observeAsState(initial = emptyList())

    TopAppBar(
        title = {
            Text("Pastelería Mil Sabores", style = MaterialTheme.typography.h6)
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = Color.White,
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                Box {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Carrito de Compras",
                        tint = Color.White
                    )
                    // Simulación de BadgedBox (Contador de Carrito)
                    if (cartCount.sumOf { it.quantity } > 0) {
                        Text(
                            text = cartCount.sumOf { it.quantity }.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PinkSoft)
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoggedIn by authViewModel.isLoggedIn.observeAsState(false)
    var showPassword by remember { mutableStateOf(false) }

    // Si el inicio de sesión es exitoso, navegar a ProductList
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.ProductList.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    PasteleriaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CreamPastel)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Bienvenido a Mil Sabores",
                style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = MaterialTheme.shapes.large,
                elevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Usuario") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Usuario") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Chocolate,
                            cursorColor = Chocolate
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Chocolate,
                            cursorColor = Chocolate
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { authViewModel.login(username, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = PinkSoft),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
                    ) {
                        Text("INGRESAR", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListItem(product: Product, productViewModel: ProductViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simulación de imagen con un icono y color
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PinkSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Cake,
                    contentDescription = "Icono de Torta",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold))
                Text(product.category, style = MaterialTheme.typography.caption.copy(color = DarkBrown.copy(alpha = 0.7f)))
                Text(formatPrice(product.price), color = Chocolate, fontWeight = FontWeight.ExtraBold)
            }

            IconButton(onClick = { productViewModel.addToCart(product) }) {
                Icon(
                    Icons.Filled.AddShoppingCart,
                    contentDescription = "Añadir al Carrito",
                    tint = Chocolate
                )
            }
        }
    }
}

@Composable
fun ProductListScreen(navController: NavController, productViewModel: ProductViewModel = viewModel()) {
    val products by productViewModel.products.observeAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    // Actualiza el filtro en el ViewModel cada vez que cambia el texto de búsqueda
    LaunchedEffect(searchQuery) {
        productViewModel.updateSearchQuery(searchQuery)
    }

    Scaffold(
        topBar = {
            AppBar(navController, productViewModel)
        },
        backgroundColor = CreamPastel
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Barra de Búsqueda
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Productos") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = PinkSoft,
                    unfocusedBorderColor = LightGrey,
                    cursorColor = PinkSoft
                )
            )

            // Listado de Productos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(products, key = { it.code }) { product ->
                    ProductListItem(product, productViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CartItemView(cartItem: CartItem, productViewModel: ProductViewModel) {
    // Implementación de la funcionalidad de "desplazar el elemento del listview" (SwipeToDismiss)
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                productViewModel.removeCartItem(cartItem.product.code)
                true
            } else {
                false
            }
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val color = Color(0xFFE57373) // Rojo suave para eliminar
            val alignment = when (dismissState.dismissDirection) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
                null -> return@SwipeToDismiss
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }
        },
        dismissContent = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                elevation = 2.dp,
                shape = MaterialTheme.shapes.small
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cartItem.product.name, style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold))
                        Text(formatPrice(cartItem.product.price), color = DarkBrown.copy(alpha = 0.8f))
                    }

                    // Botones de Modificación de Carrito
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Disminuir cantidad
                        IconButton(
                            onClick = { productViewModel.updateCartItemQuantity(cartItem.product.code, cartItem.quantity - 1) },
                            enabled = cartItem.quantity > 1
                        ) {
                            Icon(Icons.Filled.RemoveCircle, contentDescription = "Quitar", tint = Chocolate)
                        }

                        // Cantidad
                        Text(
                            text = cartItem.quantity.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontWeight = FontWeight.Bold
                        )

                        // Aumentar cantidad
                        IconButton(
                            onClick = { productViewModel.updateCartItemQuantity(cartItem.product.code, cartItem.quantity + 1) }
                        ) {
                            Icon(Icons.Filled.AddCircle, contentDescription = "Añadir", tint = Chocolate)
                        }
                    }
                }
            }
        }
    )
}


@Composable
fun CartScreen(navController: NavController, productViewModel: ProductViewModel = viewModel()) {
    val cartItems by productViewModel.cart.observeAsState(initial = emptyList())
    val total = cartItems.sumOf { it.product.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras", style = MaterialTheme.typography.h6) },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 16.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.SemiBold))
                            Text(
                                formatPrice(total),
                                style = MaterialTheme.typography.h6.copy(fontSize = 22.sp),
                                color = Chocolate
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
                            colors = ButtonDefaults.buttonColors(backgroundColor = PinkSoft),
                            shape = MaterialTheme.shapes.medium,
                            elevation = ButtonDefaults.elevation(defaultElevation = 4.dp)
                        ) {
                            Text("ENVIAR VENTA Y REGISTRAR EN BD", color = Color.White)
                        }
                    }
                }
            }
        },
        backgroundColor = CreamPastel
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
                    style = MaterialTheme.typography.h6.copy(textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                    color = DarkBrown.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = paddingValues.calculateBottomPadding() + 8.dp)
            ) {
                items(cartItems, key = { it.product.code }) { item ->
                    CartItemView(item, productViewModel)
                }
            }
        }
    }
}

// -------------------------------------------------------------------
// 6. ACTIVITY PRINCIPAL
// -------------------------------------------------------------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            PasteleriaTheme {
                NavHost(navController = navController, startDestination = Screen.Login.route) {
                    composable(Screen.Login.route) {
                        LoginScreen(navController = navController)
                    }
                    composable(Screen.ProductList.route) {
                        ProductListScreen(navController = navController)
                    }
                    composable(Screen.Cart.route) {
                        CartScreen(navController = navController)
                    }
                }
            }
        }
    }
}