package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jadapache.task2hacer.data.Task2HacerDB
import com.jadapache.task2hacer.data.repository.TareaRepository
import com.jadapache.task2hacer.data.repository.TareaRepositoryFirebaseImpl
import com.jadapache.task2hacer.data.repository.UsuarioRepository
import com.jadapache.task2hacer.data.repository.UsuarioRepositoryFirebaseImpl

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
        val tareaRepositoryFirebase = TareaRepositoryFirebaseImpl()
        val usuarioRepositoryFirebase = UsuarioRepositoryFirebaseImpl()

        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(application, tareaRepositoryLocal, tareaRepositoryFirebase) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(application, usuarioRepositoryLocal, usuarioRepositoryFirebase) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}