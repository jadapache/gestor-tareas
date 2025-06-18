package com.jadapache.task2hacer.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jadapache.task2hacer.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarTareaScreen(navController: NavController, viewModel: MainViewModel, tareaId: Int) {
    val tarea = viewModel.tareas.collectAsState().value.find { it.id == tareaId }
    var nombre by remember { mutableStateOf(tarea?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(tarea?.descripcion ?: "") }
    var toastMessage by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Editar Tarea") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                placeholder = { Text("Ej: Diseño de UI App") },
                supportingText = { Text("Se sugiere un nombre conciso") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                placeholder = { Text("Ej: hacer aplicación móvil") },
                supportingText = { Text("Incluya información tanto considere necesario") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 5,
                maxLines = 10
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Button(onClick = { navController.popBackStack()},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)) {
                    Text("Regresar")
                }
                Button(onClick = {
                    viewModel.modificarTarea(tareaId, nombre, descripcion)
                    toastMessage = "Tarea modificada"
                    navController.popBackStack()
                },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary ),
                    enabled = nombre.isNotBlank()) {
                    Text("Actualizar")
                }
            }
        }
    }
} 