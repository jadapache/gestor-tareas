package com.example.gestordetareas

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.gestordetareas.ui.theme.GestorDeTareasTheme
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
                // Aquí, si la tarea está completada y haces clic, puedes decidir si quieres editarla
                // o si el clic solo "desmarca" la tarea.
                // Por ahora, navegaremos a la edición si la tarea está pendiente,
                // y la desmarcaremos si ya está completada (como en tu imagen de referencia, el click en completada desmarca).
                if (tarea.completada) {
                    viewModel.marcarTarea(tarea.id, false)
                    onToastMessage("Tarea reasignada")
                } else {
                    navController.navigate("editar/${tarea.id}")
                }
            }
            .heightIn(max = 120.dp) // Restringe la altura máxima de la tarjeta
            .alpha(if (tarea.completada) 0.6f else 1.0f), // Opacidad: 60% si completada, 100% si pendiente
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row( // Esta Row es la estructura principal de las columnas
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Padding interno para toda la tarjeta
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna 1: Checkbox
            Column(
                modifier = Modifier
                    .padding(end = 8.dp) // Espacio a la derecha del checkbox
                    .align(Alignment.CenterVertically) // Alineación vertical al centro de la Row principal
            ) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = { checked ->
                        viewModel.marcarTarea(tarea.id, checked)
                        onToastMessage(if (checked) "Tarea completada" else "Tarea reasignada")
                    },
                    modifier = Modifier
                    // El checkbox ya tiene un tamaño inherente y padding.
                    // Al quitarle cualquier padding extra que no sea el end,
                    // y permitir que la columna padre lo centre, suele funcionar bien.
                )
            }

            // Columna 2: Nombre, Descripción y Fecha
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio restante
                    .align(Alignment.CenterVertically) // Alineación vertical al centro de la Row principal
            ) {
                Text(
                    text = tarea.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        // No TextDecoration.LineThrough
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(navController: NavController, viewModel: MainViewModel) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var toastMessage by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registrar Tarea") })
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
                    Text("Volver")
                }
                Button(onClick = { viewModel.insertarTarea(nombre, descripcion)
                                    toastMessage = "Nueva tarea agregada"
                                    navController.popBackStack() },
                       modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary ),
                        enabled = nombre.isNotBlank()) {
                    Text("Guardar")
                }
            }
        }
    }
}

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