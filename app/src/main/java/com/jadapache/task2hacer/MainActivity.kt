package com.jadapache.task2hacer

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
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
import com.jadapache.task2hacer.viewmodel.ViewModelFactory
import com.jadapache.task2hacer.viewmodel.UserViewModel

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
    val mainViewModel: TareasViewModel = viewModel(factory = factory)
    val userViewModel: UserViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = "principal") {
        composable("principal") {
            MainScreen(navController, mainViewModel)
        }
        composable("formulario") {
            FormularioScreen(navController, mainViewModel)
        }
        composable("editar") {
            val tarea = mainViewModel.tareaSeleccionada
            if (tarea != null) {
                EditarTareaScreen(navController, mainViewModel, tarea)
            } else {
                navController.popBackStack()
            }
        }
    }
}