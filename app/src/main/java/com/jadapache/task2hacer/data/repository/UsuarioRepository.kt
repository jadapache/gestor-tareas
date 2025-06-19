package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.daos.UsuarioDao
import com.jadapache.task2hacer.data.models.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    suspend fun insertUser(usuario: Usuario) = usuarioDao.insertUser(usuario)

    suspend fun getUserById(uid: String): Usuario? = usuarioDao.getUserById(uid)

    suspend fun getUserByEmail(email: String): Usuario? = usuarioDao.getUserByEmail(email)

    fun observeUserById(uid: String): Flow<Usuario?> = usuarioDao.observeUserById(uid)

    suspend fun deleteUser(uid: String) = usuarioDao.deleteUser(uid)
} 