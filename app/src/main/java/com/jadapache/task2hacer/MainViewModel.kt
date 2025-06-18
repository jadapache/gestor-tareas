package com.jadapache.task2hacer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.data.model.Tarea
import com.jadapache.task2hacer.data.model.TareaRepository
import com.jadapache.task2hacer.data.model.AppDatabase

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TareaRepository
    val tareas: StateFlow<List<Tarea>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TareaRepository(db.tareaDao())
        tareas = repository.getAllTareas().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insertarTarea(nombre: String, descripcion: String) {
        viewModelScope.launch {
            val nuevaTarea = Tarea(nombre = nombre, descripcion = descripcion)
            repository.insertTarea(nuevaTarea)
        }
    }
    fun modificarTarea(id: Int, nombre: String, descripcion: String) {
        viewModelScope.launch {
            val tarea = repository.getTareaById(id)
            if (tarea != null) {
                repository.updateTarea(tarea.copy(nombre = nombre, descripcion = descripcion))
            }
        }
    }
    fun eliminarTarea(id: Int) {
        viewModelScope.launch {
            val tarea = repository.getTareaById(id)
            if (tarea != null) {
                repository.deleteTarea(tarea)
            }
        }
    }
    fun marcarTarea(id: Int, completada: Boolean) {
        viewModelScope.launch {
            val tarea = repository.getTareaById(id)
            if (tarea != null) {
                repository.updateTarea(tarea.copy(completada = completada))
            }
        }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return MainViewModel(application) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
} 