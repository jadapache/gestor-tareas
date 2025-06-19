package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.models.Usuario

interface IUsuarioRepository {
    suspend fun registerUser(email: String, password: String): Result<Usuario>
    suspend fun loginUser(email: String, password: String): Result<Usuario>
    suspend fun getCurrentUser(): Usuario?
    suspend fun logout()
    suspend fun insertUser(usuario: Usuario): Boolean
    suspend fun getUserByEmail(email: String): Usuario?
    suspend fun deleteUser(usuario: Usuario): Boolean
} 