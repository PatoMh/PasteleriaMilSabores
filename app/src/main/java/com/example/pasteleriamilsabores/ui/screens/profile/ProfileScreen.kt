package com.example.pasteleriamilsabores.ui.screens.profile

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.data.AuthManager
import com.example.pasteleriamilsabores.navigation.Screen
import com.example.pasteleriamilsabores.ui.components.AppBar
import com.example.pasteleriamilsabores.ui.screens.products.ProductViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : ViewModel() {

    private val authManager = AuthManager(application)

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, productViewModel: ProductViewModel) {
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current.applicationContext as Application))

    Scaffold(
        topBar = {
            AppBar(
                title = "Mi Perfil",
                navController = navController,
                productViewModel = productViewModel,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
        ) {
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Mis Datos") },
                label = { Text("Mis Datos") },
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Mis Pedidos") },
                label = { Text("Mis Pedidos") },
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Mis Direcciones") },
                label = { Text("Mis Direcciones") },
                selected = false,
                onClick = { /* TODO */ }
            )
            NavigationDrawerItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración de Cuenta") },
                label = { Text("Configuración de Cuenta") },
                selected = false,
                onClick = { /* TODO */ }
            )
            Spacer(modifier = Modifier.height(16.dp))
            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión") },
                label = { Text("Cerrar Sesión") },
                selected = false,
                onClick = {
                    profileViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

class ProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
