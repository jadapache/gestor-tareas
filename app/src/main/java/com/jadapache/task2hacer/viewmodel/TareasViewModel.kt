package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.data.repository.ITareaRepository
import kotlinx.coroutines.flow.Flow

class TareasViewModel(
    application: Application,
    private val tareaRepositorySync: ITareaRepository
) : AndroidViewModel(application) {
    val tareas: StateFlow<List<Tarea>>
    var tareaSeleccionada: Tarea? = null

    init {
        tareas = getAllTareas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private fun getAllTareas(): Flow<List<Tarea>> {
        return tareaRepositorySync.getAllTareas()
    }

    // Métodos CRUD usando solo la interfaz
    fun insertarTarea(tarea: Tarea) {
        viewModelScope.launch {
            tareaRepositorySync.insertTarea(tarea)
        }
    }
    fun modificarTarea(tarea: Tarea) {
        viewModelScope.launch {
            tareaRepositorySync.updateTarea(tarea)
        }
    }
    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            tareaRepositorySync.deleteTarea(tarea)
        }
    }
    fun marcarTarea(tarea: Tarea, completada: Boolean) {
        viewModelScope.launch {
            tareaRepositorySync.updateTarea(tarea.copy(completada = completada))
        }
    }
}
