package com.jadapache.task2hacer.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TareaRepositoryFirebase : ITareaRepository {
    private val db = FirebaseDatabase.getInstance().getReference("tareas")
    private val auth = FirebaseAuth.getInstance()

    override fun getAllTareas(): Flow<List<Tarea>> = callbackFlow {
        val listener = db.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val tareas = snapshot.children.mapNotNull { it.getValue(Tarea::class.java) }
                trySend(tareas)
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { db.removeEventListener(listener) }
    }

    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = callbackFlow {
        val listener = db.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val tareas = snapshot.children.mapNotNull { it.getValue(Tarea::class.java) }
                    trySend(tareas)
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { db.removeEventListener(listener) }
    }

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        return try {
            db.child(tarea.id).setValue(tarea).await()
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
            db.child(tarea.id).removeValue().await()
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
            val snapshot = db.orderByChild("userId").equalTo(userId).get().await()
            val tareas = snapshot.children.mapNotNull { it.getValue(Tarea::class.java) }
            tareas.forEach { deleteTarea(it) }
            true
        } catch (e: Exception) {
            false
        }
    }
}
