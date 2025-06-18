package com.jadapache.task2hacer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jadapache.task2hacer.ui.theme.GestorDeTareasTheme
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.screens.FormularioScreen
import com.jadapache.task2hacer.screens.EditarTareaScreen

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel) {
    val tasks by viewModel.tareas.collectAsState()
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
            TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
        },

        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("formulario") }) {
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
                Text(text = "No hay tareas registradas.", modifier = Modifier.padding(16.dp))
            } else {
                if (pendientesTasks.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(pendientesTasks) { tarea ->
                            TareaCard(
                                tarea = tarea,
                                navController = navController,
                                viewModel = viewModel,
                                onToastMessage = { msg -> toastMessage = msg }
                            )
                        }
                    }
                }
                if (completedTasks.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completadas (${completedTasks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 8.dp) // Alinea el texto a la izquierda
                        )
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(completedTasks) { tarea ->
                            TareaCard(
                                tarea = tarea,
                                navController = navController,
                                viewModel = viewModel,
                                onToastMessage = { msg -> toastMessage = msg }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TareaCard(
    tarea: Tarea,
    navController: NavController,
    viewModel: MainViewModel,
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
                    viewModel.eliminarTarea(tarea.id)
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
                    viewModel.marcarTarea(tarea.id, false)
                    onToastMessage("Tarea reasignada")
                } else {
                    navController.navigate("editar/${tarea.id}")
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
                        viewModel.marcarTarea(tarea.id, checked)
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
                    Spacer(modifier = Modifier.height(4.dp)) // Espacio entre nombre y descripción
                    Text(
                        text = tarea.descripcion,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
                // Mostrar la fecha de creación
                //if (tarea.fechaCreacion != null) { // Asegúrate de que tu Tarea tenga este campo
                //    Spacer(modifier = Modifier.height(4.dp))
                //    Text(
                 //       text = tarea.fechaCreacion.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm")),
                 //       style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)),
                 //   )
                //}
            }

            // Columna 3: Papelera
            Column(
                modifier = Modifier
                    .padding(start = 8.dp) // Espacio a la izquierda de la papelera
                    .align(Alignment.CenterVertically) // Alineación vertical al centro de la Row principal
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = MainViewModel.provideFactory(LocalContext.current.applicationContext as android.app.Application))
    NavHost(navController = navController, startDestination = "principal") {
        composable("principal") {
            MainScreen(navController, viewModel)
        }
        composable("formulario") {
            FormularioScreen(navController, viewModel)
        }
        composable("editar/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            if (id != null) {
                EditarTareaScreen(navController, viewModel, id)
            }
        }
    }
}