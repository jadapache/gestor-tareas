package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.daos.TareaDao
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val tareaDao: TareaDao) {
    fun getAllTareas(): Flow<List<Tarea>> = tareaDao.getAll()
    fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = tareaDao.getAllTareasByUser(userId)

    suspend fun insertTarea(tarea: Tarea) = tareaDao.insert(tarea)

    suspend fun insertTareas(tasks: List<Tarea>) = tareaDao.bulkInsert(tasks)

    suspend fun updateTarea(tarea: Tarea) = tareaDao.update(tarea)

    suspend fun deleteTarea(task: Tarea) = tareaDao.deleteTarea(task)

    suspend fun deleteTareas(tasks: List<Tarea>) = tareaDao.bulkDelete(tasks)

    suspend fun deleteAllTareasForUser(userId: String) = tareaDao.deleteAllTareasForUser(userId)
}