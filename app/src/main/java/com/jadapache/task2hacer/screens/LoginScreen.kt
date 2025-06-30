package com.jadapache.task2hacer.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jadapache.task2hacer.viewmodel.UserViewModel
import com.jadapache.task2hacer.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val factory = ViewModelFactory(context.applicationContext as android.app.Application)
    val userViewModel: UserViewModel = viewModel(factory = factory)
    val usuario by userViewModel.usuario.collectAsState()
    val operationError = userViewModel.operationError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Correo y contraseña no pueden estar vacíos."
                        isLoading = false // Se emplea para detener la carga
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    userViewModel.loginUser(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading // Se deshabilita botón en la carga
            ) {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("registro") }) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }
    }

    LaunchedEffect(usuario) {
        if (usuario != null) {
            isLoading = false
            navController.navigate("principal") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    LaunchedEffect(operationError) {
        if (!operationError.isNullOrBlank()) {
            isLoading = false
            Toast.makeText(context, operationError, Toast.LENGTH_LONG).show()
            userViewModel.clearRegistrationError()
        }
    }
}