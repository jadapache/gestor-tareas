package com.example.gestordetareas

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import androidx.core.content.edit

data class Tarea(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val completada: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("tareas_prefs", Context.MODE_PRIVATE)
    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas

    init {
        cargarTareas()
    }

    private fun cargarTareas() {
        val tareasJson = sharedPreferences.getString("tareas", null)
        if (tareasJson != null) {
            val tareasList = mutableListOf<Tarea>()
            val jsonArray = JSONArray(tareasJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                tareasList.add(
                    Tarea(
                        id = obj.getInt("id"),
                        nombre = obj.getString("nombre"),
                        descripcion = obj.getString("descripcion"),
                        completada = obj.getBoolean("completada")
                    )
                )
            }
            _tareas.value = tareasList
        }
    }

    private fun guardarTareas() {
        val jsonArray = JSONArray()
        _tareas.value.forEach { tarea ->
            val obj = JSONObject()
            obj.put("id", tarea.id)
            obj.put("nombre", tarea.nombre)
            obj.put("descripcion", tarea.descripcion)
            obj.put("completada", tarea.completada)
            jsonArray.put(obj)
        }
        sharedPreferences.edit { putString("tareas", jsonArray.toString()) }
    }

    fun insertarTarea(nombre: String, descripcion: String) {
        viewModelScope.launch {
            val nuevaTarea = Tarea(
                id = (_tareas.value.maxOfOrNull { it.id } ?: 0) + 1,
                nombre = nombre,
                descripcion = descripcion
            )
            _tareas.value = listOf(nuevaTarea) + _tareas.value
            guardarTareas()
        }
    }
    fun modificarTarea(id: Int, nombre: String, descripcion: String) {
        viewModelScope.launch {
            _tareas.value = _tareas.value.map {
                if (it.id == id) it.copy(nombre = nombre, descripcion = descripcion) else it
            }
            guardarTareas()
        }
    }
    fun eliminarTarea(id: Int) {
        viewModelScope.launch {
            _tareas.value = _tareas.value.filter { it.id != id }
            guardarTareas()
        }
    }

    fun marcarTarea(id: Int, completada: Boolean) {
        viewModelScope.launch {
            _tareas.value = _tareas.value.map {
                if (it.id == id) it.copy(completada = completada) else it
            }
            guardarTareas()
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