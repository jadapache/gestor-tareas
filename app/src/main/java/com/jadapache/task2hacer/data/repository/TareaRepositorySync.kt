package com.jadapache.task2hacer.data.repository

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
    private val appContext: android.content.Context
) : ITareaRepository {
    init {
        // Lanzar sincronización automática al detectar conexión
        CoroutineScope(Dispatchers.IO).launch {
            if (isInternetAvailable(appContext)) {
                sincronizarDesdeFirebase()
                subirCambiosLocales()
            }
        }
    }

    override fun getAllTareas(): Flow<List<Tarea>> = localRepo.getAllTareas()
    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = localRepo.getAllTareasByUser(userId)

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        val local = localRepo.insertTarea(tarea)
        if (isInternetAvailable(appContext)) remoteRepo.insertTarea(tarea)
        return local
    }
    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        val local = localRepo.insertTareas(tasks)
        if (isInternetAvailable(appContext)) remoteRepo.insertTareas(tasks)
        return local
    }
    override suspend fun updateTarea(tarea: Tarea): Boolean {
        val local = localRepo.updateTarea(tarea)
        if (isInternetAvailable(appContext)) remoteRepo.updateTarea(tarea)
        return local
    }
    override suspend fun deleteTarea(tarea: Tarea): Boolean {
        val local = localRepo.deleteTarea(tarea)
        if (isInternetAvailable(appContext)) remoteRepo.deleteTarea(tarea)
        return local
    }
    override suspend fun deleteTareas(tasks: List<Tarea>): Boolean {
        val local = localRepo.deleteTareas(tasks)
        if (isInternetAvailable(appContext)) remoteRepo.deleteTareas(tasks)
        return local
    }
    override suspend fun deleteAllTareasForUser(userId: String): Boolean {
        val local = localRepo.deleteAllTareasForUser(userId)
        if (isInternetAvailable(appContext)) remoteRepo.deleteAllTareasForUser(userId)
        return local
    }

    // Sincroniza desde Firebase a local
    suspend fun sincronizarDesdeFirebase() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val remoteTareas = remoteRepo.getAllTareasByUser(userId).first()
        localRepo.deleteAllTareasForUser(userId)
        localRepo.insertTareas(remoteTareas)
    }

    // Sube tareas locales a Firebase (solo ejemplo, puedes mejorar la lógica de conflictos)
    suspend fun subirCambiosLocales() {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val localTareas = localRepo.getAllTareasByUser(userId).first()
        remoteRepo.deleteAllTareasForUser(userId)
        remoteRepo.insertTareas(localTareas)
    }
} 