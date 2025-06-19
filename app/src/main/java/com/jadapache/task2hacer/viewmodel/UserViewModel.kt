package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jadapache.task2hacer.data.models.Usuario
import com.jadapache.task2hacer.data.repository.UsuarioRepository
import com.jadapache.task2hacer.data.repository.UsuarioRepositoryFirebaseImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.utils.isInternetAvailable

class UserViewModel(
    application: Application,
    private val usuarioRepositoryLocal: UsuarioRepository,
    private val usuarioRepositoryFirebase: UsuarioRepositoryFirebaseImpl
) : AndroidViewModel(application) {
    private val repository: Any
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    init {
        repository = if (isInternetAvailable(application.applicationContext)) {
            usuarioRepositoryFirebase
        } else {
            usuarioRepositoryLocal
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            if (repository is UsuarioRepositoryFirebaseImpl) {
                val result = repository.registerUser(email, password)
                _usuario.value = result.getOrNull()
            } else if (repository is UsuarioRepository) {
                val usuario = Usuario(id = email, email = email, pass = password)
                repository.insertUser(usuario)
                _usuario.value = usuario
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            if (repository is UsuarioRepositoryFirebaseImpl) {
                val result = repository.loginUser(email, password)
                _usuario.value = result.getOrNull()
            } else if (repository is UsuarioRepository) {
                val usuario = repository.getUserByEmail(email)
                _usuario.value = if (usuario != null && usuario.pass == password) usuario else null
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            if (repository is UsuarioRepositoryFirebaseImpl) {
                _usuario.value = repository.getCurrentUser()
            } else if (repository is UsuarioRepository) {
                // No hay sesión persistente local, solo último usuario logueado
            }
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            if (repository is UsuarioRepository) {
                repository.deleteUser(uid)
            }
            // En Firebase, normalmente se desactiva la cuenta desde Auth, no desde aquí
        }
    }
}
