package com.jadapache.task2hacer.screens

import android.app.Application
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jadapache.task2hacer.utils.isInternetAvailable
import com.jadapache.task2hacer.viewmodel.UserViewModel
import com.jadapache.task2hacer.viewmodel.ViewModelFactory

@Composable
fun RegisterScreen(navController: NavController) {
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current.applicationContext as Application
    val factory = ViewModelFactory(context)
    val userViewModel: UserViewModel = viewModel(factory = factory)
    val usuario by userViewModel.usuario.collectAsState()
    val registrationError = userViewModel.operationError


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Crear Cuenta", style = MaterialTheme.typography.headlineMedium)
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || fullname.isBlank()) {
                        errorMessage = "Todos los campos son obligatorios."
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden."
                        return@Button
                    }
                    isLoading = true
                    errorMessage = null
                    isLoading = true
                    errorMessage = null
                    if (!isInternetAvailable(context)) {
                        isLoading = false
                        errorMessage = "Sin conexión a internet."
                        return@Button
                    }
                    userViewModel.registerUser(email, password, fullname)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } }) {
            Text("¿Ya tienes cuenta? Inicia sesión aquí")
        }
    }

    LaunchedEffect(usuario) {
        if (usuario != null) {
            Toast.makeText(context, "Registro exitoso.", Toast.LENGTH_SHORT).show()
            navController.navigate("principal") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(registrationError) {
        if (!registrationError.isNullOrBlank()) {
            isLoading = false
            Toast.makeText(context, registrationError, Toast.LENGTH_LONG).show()
            userViewModel.clearRegistrationError()
        }
    }


    LaunchedEffect(errorMessage) {
        if (errorMessage != null && errorMessage!!.isNotBlank()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }



}