package com.jadapache.task2hacer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.jadapache.task2hacer.utils.encriptationUtil
import android.content.Context

class TareaRepositoryFirebase(private val context: Context) : ITareaRepository {
    private val db = FirebaseFirestore.getInstance().collection("tareas")

    override fun getAllTareas(): Flow<List<Tarea>> = callbackFlow {
        val listener = db.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val tareas = snapshot?.toObjects(Tarea::class.java)?.map {
                it.copy(descripcion = encriptationUtil.decrypt(context, it.descripcion))
            } ?: emptyList()
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
            val tareas = snapshot?.toObjects(Tarea::class.java)?.map {
                it.copy(descripcion = encriptationUtil.decrypt(context, it.descripcion))
            } ?: emptyList()
            trySend(tareas)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        return try {
            val encryptedDesc = encriptationUtil.encrypt(context, tarea.descripcion)
            db.document(tarea.id).set(tarea.copy(descripcion = encryptedDesc)).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        return try {
            tasks.forEach {
                val encryptedDesc = encriptationUtil.encrypt(context, it.descripcion)
                insertTarea(it.copy(descripcion = encryptedDesc))
            }
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