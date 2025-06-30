package com.jadapache.task2hacer.data.repository

import android.content.Context
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.utils.isInternetAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TareaRepositorySync(
    private val localRepo: ITareaRepository,
    private val remoteRepo: ITareaRepository,
    private val context: Context
) : ITareaRepository {
    init {
        // Lanzar sincronización automática al detectar conexión
        CoroutineScope(Dispatchers.IO).launch {
            if (isInternetAvailable(context)) {
                sincronizarDesdeFirebase()
                subirCambiosLocales()
            }
        }
    }

    override fun getAllTareas(): Flow<List<Tarea>> = localRepo.getAllTareas()
    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = localRepo.getAllTareasByUser(userId)

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        val local = localRepo.insertTarea(tarea)
        if (isInternetAvailable(context)) remoteRepo.insertTarea(tarea)
        return local
    }
    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        val local = localRepo.insertTareas(tasks)
        if (isInternetAvailable(context)) remoteRepo.insertTareas(tasks)
        return local
    }
    override suspend fun updateTarea(tarea: Tarea): Boolean {
        val local = localRepo.updateTarea(tarea)
        if (isInternetAvailable(context)) remoteRepo.updateTarea(tarea)
        return local
    }
    override suspend fun deleteTarea(tarea: Tarea): Boolean {
        val local = localRepo.deleteTarea(tarea)
        if (isInternetAvailable(context)) remoteRepo.deleteTarea(tarea)
        return local
    }
    override suspend fun deleteTareas(tasks: List<Tarea>): Boolean {
        val local = localRepo.deleteTareas(tasks)
        if (isInternetAvailable(context)) remoteRepo.deleteTareas(tasks)
        return local
    }
    override suspend fun deleteAllTareasForUser(userId: String): Boolean {
        val local = localRepo.deleteAllTareasForUser(userId)
        if (isInternetAvailable(context)) remoteRepo.deleteAllTareasForUser(userId)
        return local
    }

    // Sincroniza desde Firebase a local
    suspend fun sincronizarDesdeFirebase() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val remoteTareas = remoteRepo.getAllTareasByUser(userId).first()
        val localTareas = localRepo.getAllTareasByUser(userId).first()

        val localMap = localTareas.associateBy { it.id }

        val nuevas = remoteTareas.filter { it.id !in localMap }
        val actualizables = remoteTareas.filter { tareaRemota ->
            val tareaLocal = localMap[tareaRemota.id]
            tareaLocal != null && tareaLocal != tareaRemota
        }

        if (nuevas.isNotEmpty()) {
            localRepo.insertTareas(nuevas)
        }
        actualizables.forEach { tarea ->
            localRepo.updateTarea(tarea)
        }
    }

    // Sube tareas locales a Firebase
    suspend fun subirCambiosLocales() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val localTareas = localRepo.getAllTareasByUser(userId).first()
        val remoteTareas = remoteRepo.getAllTareasByUser(userId).first()

        val remoteMap = remoteTareas.associateBy { it.id }

        val nuevas = localTareas.filter { it.id !in remoteMap }
        val actualizables = localTareas.filter { tareaLocal ->
            val tareaRemota = remoteMap[tareaLocal.id]
            tareaRemota != null && tareaRemota != tareaLocal
        }

        if (nuevas.isNotEmpty()) {
            remoteRepo.insertTareas(nuevas)
        }
        actualizables.forEach { tarea ->
            remoteRepo.updateTarea(tarea)
        }
    }
} 