package com.jadapache.task2hacer

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jadapache.task2hacer.ui.theme.GestorDeTareasTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.jadapache.task2hacer.screens.FormularioScreen
import com.jadapache.task2hacer.screens.EditarTareaScreen
import com.jadapache.task2hacer.viewmodel.TareasViewModel
import com.jadapache.task2hacer.screens.MainScreen
import com.jadapache.task2hacer.screens.LoginScreen
import com.jadapache.task2hacer.viewmodel.ViewModelFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.jadapache.task2hacer.data.Task2HacerDB
import com.google.firebase.auth.FirebaseAuth
import com.jadapache.task2hacer.screens.RegisterScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestorDeTareasTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val factory = ViewModelFactory(context)
    val tareasViewModel: TareasViewModel = viewModel(factory = factory)

    var startDestination by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        startDestination = if (firebaseUser != null) "principal" else "login"
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable("principal") {
                MainScreen(navController, tareasViewModel)
            }
            composable("formulario") {
                FormularioScreen(navController, tareasViewModel)
            }
            composable("editar") {
                val tarea = tareasViewModel.tareaSeleccionada
                if (tarea != null) {
                    EditarTareaScreen(navController, tareasViewModel, tarea)
                } else {
                    navController.popBackStack("principal", inclusive = false)
                }
            }
            composable("login") {
                LoginScreen(navController, FirebaseAuth.getInstance())
            }
            composable("registro") {
                RegisterScreen(navController)
            }
        }
    }
}