package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.data.repository.TareaRepository
import com.jadapache.task2hacer.data.repository.TareaRepositoryFirebaseImpl
import com.jadapache.task2hacer.utils.isInternetAvailable
import kotlinx.coroutines.flow.Flow

class MainViewModel(
    application: Application,
    private val tareaRepositoryLocal: TareaRepository,
    private val tareaRepositoryFirebase: TareaRepositoryFirebaseImpl
) : AndroidViewModel(application) {
    private val repository: Any
    val tareas: StateFlow<List<Tarea>>
    var tareaSeleccionada: Tarea? = null

    init {
        // Usa la función importada y pasa el contexto de la aplicación
        repository = if (isInternetAvailable(application.applicationContext)) {
            tareaRepositoryFirebase
        } else {
            tareaRepositoryLocal // Asumo que este era el repositorio para el caso 'else'
        }

        // Esta inicialización de 'tareas' debe estar fuera del 'else'
        // y debe usar el 'repository' que acabas de asignar.
        tareas = getAllTareas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    private fun getAllTareas(): Flow<List<Tarea>> {
        return if (repository is TareaRepositoryFirebaseImpl) {
            // Aquí deberías obtener el userId actual de Firebase Auth
            val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
            repository.getAllTareasByUser(userId)
        } else if (repository is TareaRepository) {
            // Aquí deberías obtener el userId local (si aplica)
            val userId = "local_user" // Ajusta según tu lógica
            repository.getAllTareas()
        } else {
            throw IllegalStateException("Repositorio no soportado")
        }
    }

    // Métodos CRUD adaptados para ambos repositorios
    fun insertarTarea(tarea: Tarea) {
        viewModelScope.launch {
            if (repository is TareaRepositoryFirebaseImpl) {
                repository.insertTarea(tarea)
            } else if (repository is TareaRepository) {
                repository.insertTarea(tarea)
            }
        }
    }
    fun modificarTarea(tarea: Tarea) {
        viewModelScope.launch {
            if (repository is TareaRepositoryFirebaseImpl) {
                repository.updateTarea(tarea)
            } else if (repository is TareaRepository) {
                repository.updateTarea(tarea)
            }
        }
    }
    fun eliminarTarea(tarea: Tarea) {
        viewModelScope.launch {
            if (repository is TareaRepositoryFirebaseImpl) {
                repository.deleteTarea(tarea)
            } else if (repository is TareaRepository) {
                repository.deleteTarea(tarea)
            }
        }
    }
    fun marcarTarea(tarea: Tarea, completada: Boolean) {
        viewModelScope.launch {
            if (repository is TareaRepositoryFirebaseImpl) {
                repository.updateTarea(tarea.copy(completada = completada))
            } else if (repository is TareaRepository) {
                repository.updateTarea(tarea.copy(completada = completada))
            }
        }
    }

}
