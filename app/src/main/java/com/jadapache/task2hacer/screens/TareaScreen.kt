package com.jadapache.task2hacer.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.utils.formatDate
import com.jadapache.task2hacer.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaScreen(
    navController: NavController,
    viewModel: TareasViewModel,
    tareaId: String?
) {
    val isNewTask = tareaId == null
    val task = if (isNewTask) null else viewModel.tareas.collectAsState().value.find { it.id == tareaId }
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var nombre by remember { mutableStateOf(task?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(task?.descripcion ?: "") }
    var ubicacion by remember {
        mutableStateOf(
            task?.ubicacion?.split(",")?.let { LatLng(it[0].toDouble(), it[1].toDouble()) }
        )
    }

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
            if (isGranted) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    ubicacion = if (location != null) {
                        LatLng(location.latitude, location.longitude)
                    } else {
                        LatLng(7.061158711284572,-73.10883332043886) // Default location
                    }
                }
            }
        }
    )

    var showDeleteButton by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var modoEdicionUbicacion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val newLocation = ubicacion?.let { "${it.latitude},${it.longitude}" }
                            if (isNewTask) {
                                viewModel.insertarTarea(tarea = Tarea(nombre = nombre, descripcion = descripcion, userId = userId.toString()))
                            } else {
                                task?.let {
                                    viewModel.modificarTarea(it.copy(nombre = nombre, descripcion = descripcion, ubicacion = newLocation))
                                }
                            }
                            navController.popBackStack()
                        },
                        enabled = nombre.isNotBlank()
                    ) {
                        Text("Listo", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (hasLocationPermission) {
                    modoEdicionUbicacion = true
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        ubicacion = if (location != null) {
                            LatLng(location.latitude, location.longitude)
                        } else {
                            LatLng(7.061158711284572,-73.10883332043886)
                        }
                    }
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }) {
                Icon(Icons.Default.LocationOn, contentDescription = "Agregar o actualizar ubicación")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = nombre,
                onValueChange = { nombre = it },
                textStyle = TextStyle(fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (nombre.isEmpty()) {
                            Text("Nombre de Tarea", style = TextStyle(fontSize = 24.sp, color = Color.Gray))
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${formatDate(task?.ultimaMod ?: System.currentTimeMillis())} | ${descripcion.length} caracteres",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (descripcion.isEmpty()) {
                            Text("Descripción de la tarea", style = TextStyle(fontSize = 16.sp, color = Color.Gray))
                        }
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (ubicacion != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(ubicacion!!, 15f)
                    }
                    val markerState = rememberMarkerState(position = ubicacion!!)

                    LaunchedEffect(ubicacion) {
                        ubicacion?.let {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                            markerState.position = it
                        }
                    }

                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { newLatLng ->
                            if (modoEdicionUbicacion) {
                                ubicacion = newLatLng
                                markerState.position = newLatLng
                                modoEdicionUbicacion = false
                            }
                        },
                        uiSettings = if (modoEdicionUbicacion)
                            MapUiSettings(zoomControlsEnabled = true, scrollGesturesEnabled = true, zoomGesturesEnabled = true, tiltGesturesEnabled = true, rotationGesturesEnabled = true)
                        else
                            MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false, zoomGesturesEnabled = false, tiltGesturesEnabled = false, rotationGesturesEnabled = false)
                    ) {
                        Marker(state = markerState, title = "Ubicación")
                    }
                    if (!modoEdicionUbicacion) {
                        IconButton(
                            onClick = {
                                showConfirmDialog = true
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar ubicación",
                                tint = Color.Red
                            )
                        }
                    }
                }
                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = { Text("Eliminar ubicación") },
                        text = { Text("¿Estás seguro de que deseas eliminar la ubicación?") },
                        confirmButton = {
                            TextButton(onClick = {
                                ubicacion = null
                                showDeleteButton = false
                                showConfirmDialog = false
                                modoEdicionUbicacion = false
                            }) {
                                Text("Sí")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmDialog = false }) {
                                Text("No")
                            }
                        }
                    )
                }
            }
        }
    }
}
