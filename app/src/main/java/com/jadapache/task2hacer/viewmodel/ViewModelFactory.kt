package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jadapache.task2hacer.data.Task2HacerDB
import com.jadapache.task2hacer.data.repository.TareaRepository
import com.jadapache.task2hacer.data.repository.TareaRepositoryFirebase
import com.jadapache.task2hacer.data.repository.TareaRepositorySync
import com.jadapache.task2hacer.data.repository.UsuarioRepository
import com.jadapache.task2hacer.data.repository.UsuarioRepositoryFirebase

class ViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Obtener la instancia de la base de datos
        val db = Task2HacerDB.invoke(application.applicationContext)

        // Crear instancias de los repositorios locales
        val tareaDao = db.getTareaDao()
        val usuarioDao = db.getUsuarioDao()
        val tareaRepositoryLocal = TareaRepository(tareaDao)
        val usuarioRepositoryLocal = UsuarioRepository(usuarioDao)

        // Crear instancias de los repositorios de Firebase
        val tareaRepositoryFirebase = TareaRepositoryFirebase()
        val usuarioRepositoryFirebase = UsuarioRepositoryFirebase()

        // Crear el repositorio de sincronización
        val tareaRepositorySync = TareaRepositorySync(
            tareaRepositoryLocal,
            tareaRepositoryFirebase,
            application.applicationContext
        )

        return when {
            modelClass.isAssignableFrom(TareasViewModel::class.java) -> {
                TareasViewModel(application, tareaRepositorySync) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(application, usuarioRepositoryLocal, usuarioRepositoryFirebase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}