package com.jadapache.task2hacer.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jadapache.task2hacer.data.models.Usuario
import com.jadapache.task2hacer.data.repository.IUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.jadapache.task2hacer.utils.AESUtil

class UserViewModel(
    application: Application,
    private val usuarioRepository: IUsuarioRepository
) : AndroidViewModel(application) {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    var operationError by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set


    fun registerUser(email: String, password: String, fullname: String) {
        viewModelScope.launch {
            operationError = null
            isLoading = true

            val encryptedPassword = AESUtil.encryptPassword(getApplication(), password)
            val result = usuarioRepository.registerUser(email, encryptedPassword, fullname)
            isLoading = false

            result.fold(
                onSuccess = { usuario ->
                    _usuario.value = usuario
                },
                onFailure = { exception ->
                    operationError = exception.message ?: "Error desconocido durante el registro."
                }
            )
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            val encryptedPassword = AESUtil.encryptPassword(getApplication(), password)
            val result = usuarioRepository.loginUser(email, encryptedPassword)
            _usuario.value = result.getOrNull()
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _usuario.value = usuarioRepository.getCurrentUser()
        }
    }

    fun deleteUser(usuario: Usuario) {
        viewModelScope.launch {
            usuarioRepository.deleteUser(usuario)
        }
    }

    fun clearRegistrationError() {
        operationError = null
    }
}
