package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.flow.Flow

interface ITareaRepository {
    fun getAllTareas(): Flow<List<Tarea>>
    fun getAllTareasByUser(userId: String): Flow<List<Tarea>>
    suspend fun insertTarea(tarea: Tarea): Boolean
    suspend fun insertTareas(tasks: List<Tarea>): Boolean
    suspend fun updateTarea(tarea: Tarea): Boolean
    suspend fun deleteTarea(tarea: Tarea): Boolean
    suspend fun deleteTareas(tasks: List<Tarea>): Boolean
    suspend fun deleteAllTareasForUser(userId: String): Boolean
}