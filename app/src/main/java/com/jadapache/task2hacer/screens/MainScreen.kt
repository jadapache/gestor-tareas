package com.jadapache.task2hacer.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.viewmodel.TareasViewModel

@Composable
fun PendingTasksList(tasks: List<Tarea>, navController: NavHostController, viewModel: TareasViewModel, onToastMessage: (String) -> Unit) {
    if (tasks.isNotEmpty()) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tasks) { tarea ->
                TareaCard(
                    tarea = tarea,
                    navController = navController,
                    viewModel = viewModel,
                    onToastMessage = onToastMessage
                )
            }
        }
    }
}

@Composable
fun CompletedTasksList(tasks: List<Tarea>, navController: NavHostController, viewModel: TareasViewModel, onToastMessage: (String) -> Unit) {
    if (tasks.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Completadas (${tasks.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tasks) { tarea ->
                TareaCard(
                    tarea = tarea,
                    navController = navController,
                    viewModel = viewModel,
                    onToastMessage = onToastMessage
                )
            }
        }
    }
}

@Composable
fun NoTasksMessage() {
    Text(text = "No hay tareas registradas.", modifier = Modifier.padding(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: TareasViewModel) {
    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val tareasFlow = remember(userId) { viewModel.tareasUsuario(userId) }
    val tasks by tareasFlow.collectAsState()
    val context = LocalContext.current
    var toastMessage by remember { mutableStateOf("") }
    if (toastMessage.isNotEmpty()) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        toastMessage = ""
    }
    val pendientesTasks = tasks.filter { !it.completada }
    val completedTasks = tasks.filter { it.completada }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.jadapache.task2hacer.R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut() // Cerrar sesión
                        navController.navigate("login") { // Navegar a login
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("tareaDetail") }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            if (pendientesTasks.isEmpty() && completedTasks.isEmpty()) {
                NoTasksMessage()
            } else {
                PendingTasksList(pendientesTasks, navController, viewModel, onToastMessage = { msg -> toastMessage = msg })
                CompletedTasksList(completedTasks, navController, viewModel, onToastMessage = { msg -> toastMessage = msg })
            }
        }
    }
}

@Composable
fun TareaCard(
    tarea: Tarea,
    navController: NavController,
    viewModel: TareasViewModel,
    onToastMessage: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Está seguro que desea eliminar la tarea '${tarea.nombre}'?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarTarea(tarea)
                    onToastMessage("Tarea eliminada")
                    showDialog = false
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                if (tarea.completada) {
                    viewModel.marcarTarea(tarea, false)
                    onToastMessage("Tarea reasignada")
                } else {
                    navController.navigate("tareaDetail?tareaId=${tarea.id}")
                }
            }
            .heightIn(max = 120.dp)
            .alpha(if (tarea.completada) 0.6f else 1.0f),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna 1: Checkbox
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = { checked ->
                        viewModel.marcarTarea(tarea, checked)
                        onToastMessage(if (checked) "Tarea completada" else "Tarea reasignada")
                    },
                    modifier = Modifier
                )
            }

            // Columna 2: Nombre, Descripción y Fecha
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = tarea.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                if (tarea.descripcion.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            // Columna 3: Papelera
            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically)
            ) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Eliminar tarea",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
