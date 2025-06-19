package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.daos.UsuarioDao
import com.jadapache.task2hacer.data.models.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) : IUsuarioRepository {
    override suspend fun registerUser(email: String, password: String): Result<Usuario> {
        throw NotImplementedError("registerUser solo está implementado en Firebase")
    }

    override suspend fun loginUser(email: String, password: String): Result<Usuario> {
        throw NotImplementedError("loginUser solo está implementado en Firebase")
    }

    override suspend fun getCurrentUser(): Usuario? {
        throw NotImplementedError("getCurrentUser solo está implementado en Firebase")
    }

    override suspend fun logout() {
        throw NotImplementedError("logout solo está implementado en Firebase")
    }

    override suspend fun insertUser(usuario: Usuario): Boolean {
        usuarioDao.insertUser(usuario)
        return true
    }

    override suspend fun getUserByEmail(email: String): Usuario? {
        return usuarioDao.getUserByEmail(email)
    }

    override suspend fun deleteUser(usuario: Usuario): Boolean {
        usuarioDao.deleteUser(usuario.id)
        return true
    }

    suspend fun getUserById(uid: String): Usuario? = usuarioDao.getUserById(uid)
} 