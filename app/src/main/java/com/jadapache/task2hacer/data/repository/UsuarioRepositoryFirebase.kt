package com.jadapache.task2hacer.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jadapache.task2hacer.data.models.Usuario
import kotlinx.coroutines.tasks.await

class UsuarioRepositoryFirebase : IUsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("usuarios")

    override suspend fun registerUser(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val usuario = Usuario(id = user.uid, email = email, pass = password)
                db.child(user.uid).setValue(usuario).await()
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo registrar el usuario en Firebase"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val dataSnapshot = db.child(user.uid).get().await()
                val usuario = dataSnapshot.getValue(Usuario::class.java)
                if (usuario != null) {
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Usuario no encontrado en la base de datos"))
                }
            } else {
                Result.failure(Exception("No se pudo iniciar sesión en Firebase"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): Usuario? {
        val user = auth.currentUser ?: return null
        val dataSnapshot = db.child(user.uid).get().await()
        return dataSnapshot.getValue(Usuario::class.java)
    }

    override suspend fun logout() {
        auth.signOut()
    }

    // Métodos de la interfaz para compatibilidad
    override suspend fun insertUser(usuario: Usuario): Boolean {
        db.child(usuario.id).setValue(usuario).await()
        return true
    }

    override suspend fun getUserByEmail(email: String): Usuario? {
        val snapshot = db.orderByChild("email").equalTo(email).get().await()
        return snapshot.children.firstOrNull()?.getValue(Usuario::class.java)
    }

    override suspend fun deleteUser(usuario: Usuario): Boolean {
        db.child(usuario.id).removeValue().await()
        return true
    }
} 