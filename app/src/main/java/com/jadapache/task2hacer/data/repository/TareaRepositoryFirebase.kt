package com.jadapache.task2hacer.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TareaRepositoryFirebase : ITareaRepository {
    private val db = FirebaseFirestore.getInstance().collection("tareas")
    private val auth = FirebaseAuth.getInstance()

    override fun getAllTareas(): Flow<List<Tarea>> = callbackFlow {
        val listener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val tareas = snapshot?.toObjects(Tarea::class.java) ?: emptyList()
            trySend(tareas)
        }
        awaitClose { listener.remove() }
    }

    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = callbackFlow {
        val query = db.whereEqualTo("userId", userId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val tareas = snapshot?.toObjects(Tarea::class.java) ?: emptyList()
            trySend(tareas)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        return try {
            db.document(tarea.id).set(tarea).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        return try {
            tasks.forEach { insertTarea(it) }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateTarea(tarea: Tarea): Boolean {
        return insertTarea(tarea)
    }

    override suspend fun deleteTarea(tarea: Tarea): Boolean {
        return try {
            db.document(tarea.id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteTareas(tasks: List<Tarea>): Boolean {
        return try {
            tasks.forEach { deleteTarea(it) }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteAllTareasForUser(userId: String): Boolean {
        return try {
            val snapshot = db.whereEqualTo("userId", userId).get().await()
            val tareas = snapshot.toObjects(Tarea::class.java)
            tareas.forEach { deleteTarea(it) }
            true
        } catch (e: Exception) {
            false
        }
    }
}