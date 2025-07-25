package com.jadapache.task2hacer.data.repository

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.jadapache.task2hacer.data.daos.UsuarioDao
import com.jadapache.task2hacer.data.models.Usuario
import com.jadapache.task2hacer.utils.userKeyUtil
import kotlinx.coroutines.tasks.await
import java.io.IOException

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val context: Context
) : IUsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance().collection("usuarios")

    override suspend fun registerUser(email: String, password: String, fullname: String): Result<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val usuario = Usuario(id = user.uid, email = email, fullname = fullname)
                db.document(user.uid).set(usuario).await()
                usuarioDao.insertUser(usuario)
                userKeyUtil.getOrCreateUserKey(context)
                Result.success(usuario)
            } else {
                Result.failure(Exception("No se pudo registrar el usuario en Firebase."))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is FirebaseNetworkException -> "Error: ${e.localizedMessage}"
                is FirebaseAuthException -> {
                    "Error: ${e.cause ?: e.errorCode}"
                }
                is IOException -> {
                    "Error. Por favor inténta de nuevo más tarde."
                }
                else -> {
                    "Ocurrió un error inesperado: ${e.localizedMessage ?: "Error desconocido"}"
                }
            }
            Result.failure(Exception(errorMessage, e))
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Usuario> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                // Descargar y almacenar la clave única del usuario
                userKeyUtil.getOrCreateUserKey(context)
                //Se lee los datos del usuario desde FireStore y se serializa en un objeto Usuario
                val dataSnapshot = db.document(user.uid).get().await()
                val usuario = try {
                    dataSnapshot.toObject(Usuario::class.java)
                } catch (e: Exception) { //Se agrega manejo de excepciones
                    null
                }
                if (usuario != null) {
                    usuarioDao.insertUser(usuario) // Guardar localmente
                    Result.success(usuario)
                } else {
                    Result.failure(Exception("Error al leer los datos del usuario desde la nube."))
                }
            } else {
                Result.failure(Exception("No se pudo iniciar sesión."))
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                    "No existe una cuenta con este correo electrónico."
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                    "Email o contraseña incorrecta."
                else -> e.localizedMessage ?: "Error de autenticación desconocido."
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun getCurrentUser(): Usuario? {
        val user = auth.currentUser
        return if (user != null) {
            val dataSnapshot = db.document(user.uid).get().await()
            val usuario = dataSnapshot.toObject(Usuario::class.java)
            usuarioDao.insertUser(usuario!!) // Actualizar local
            usuario
        } else {
            null
        }
    }

    override suspend fun logout() {
        val user = auth.currentUser
        if (user != null) {
            usuarioDao.deleteUser(user.uid)
        }
        auth.signOut()
    }

    override suspend fun insertUser(usuario: Usuario): Boolean {
        db.document(usuario.id).set(usuario).await()
        usuarioDao.insertUser(usuario)
        return true
    }

    override suspend fun getUserByEmail(email: String): Usuario? {
        val snapshot = db.whereEqualTo("email", email).get().await()
        val usuario = snapshot.documents.firstOrNull()?.toObject(Usuario::class.java)
        if (usuario != null) usuarioDao.insertUser(usuario)
        return usuario
    }

    override suspend fun deleteUser(usuario: Usuario): Boolean {
        db.document(usuario.id).delete().await()
        usuarioDao.deleteUser(usuario.id)
        return true
    }
} 