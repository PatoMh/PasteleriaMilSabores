package com.example.pasteleriamilsabores.ui.screens.register

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pasteleriamilsabores.data.AuthManager
import com.example.pasteleriamilsabores.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : ViewModel() {
    var names by mutableStateOf("")
    var lastNames by mutableStateOf("")
    var email by mutableStateOf("")
    var age by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var showPasswordMismatchDialog by mutableStateOf(false)

    private val authManager = AuthManager(application)

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess = _registrationSuccess.asStateFlow()

    fun onRegister() {
        if (password != confirmPassword) {
            showPasswordMismatchDialog = true
            return
        }
        if (names.isNotBlank() && lastNames.isNotBlank() && email.isNotBlank() && age.isNotBlank() && password.isNotBlank()) {
            viewModelScope.launch {
                authManager.login()
                _registrationSuccess.value = true
            }
        } else {
            // Opcional: Mostrar un mensaje de que todos los campos son requeridos
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModelFactory(LocalContext.current.applicationContext as Application))) {

    val registrationSuccess by registerViewModel.registrationSuccess.collectAsState()

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            navController.navigate(Screen.Home.route) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = registerViewModel.names,
                onValueChange = { registerViewModel.names = it },
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = registerViewModel.lastNames,
                onValueChange = { registerViewModel.lastNames = it },
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = registerViewModel.email,
                onValueChange = { registerViewModel.email = it },
                label = { Text("Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = registerViewModel.age,
                onValueChange = { registerViewModel.age = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = registerViewModel.password,
                onValueChange = { registerViewModel.password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = registerViewModel.confirmPassword,
                onValueChange = { registerViewModel.confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { registerViewModel.onRegister() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            if (registerViewModel.showPasswordMismatchDialog) {
                AlertDialog(
                    onDismissRequest = { registerViewModel.showPasswordMismatchDialog = false },
                    title = { Text("Error de Contraseña") },
                    text = { Text("Las contraseñas no coinciden. Por favor, inténtalo de nuevo.") },
                    confirmButton = {
                        Button(
                            onClick = { registerViewModel.showPasswordMismatchDialog = false }
                        ) {
                            Text("Aceptar")
                        }
                    }
                )
            }
        }
    }
}

class RegisterViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
