package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jadapache.task2hacer.data.models.Usuario
import com.jadapache.task2hacer.data.repository.IUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.utils.isInternetAvailable

class UserViewModel(
    application: Application,
    private val usuarioRepositoryLocal: IUsuarioRepository,
    private val usuarioRepositoryFirebase: IUsuarioRepository
) : AndroidViewModel(application) {
    private val repository: IUsuarioRepository = if (isInternetAvailable(application.applicationContext)) {
        usuarioRepositoryFirebase
    } else {
        usuarioRepositoryLocal
    }
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(email, password)
            _usuario.value = result.getOrNull()
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _usuario.value = result.getOrNull()
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _usuario.value = repository.getCurrentUser()
        }
    }

    fun deleteUser(usuario: Usuario) {
        viewModelScope.launch {
            repository.deleteUser(usuario)
        }
    }
}
