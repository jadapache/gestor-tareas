package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.daos.TareaDao
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val tareaDao: TareaDao) : ITareaRepository {
    override fun getAllTareas(): Flow<List<Tarea>> = tareaDao.getAll()
    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = tareaDao.getAllTareasByUser(userId)

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        tareaDao.insert(tarea)
        return true
    }

    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        tareaDao.bulkInsert(tasks)
        return true
    }

    override suspend fun updateTarea(tarea: Tarea): Boolean {
        tareaDao.update(tarea)
        return true
    }

    override suspend fun deleteTarea(tarea: Tarea): Boolean {
        tareaDao.deleteTarea(tarea)
        return true
    }

    override suspend fun deleteTareas(tasks: List<Tarea>): Boolean {
        tareaDao.bulkDelete(tasks)
        return true
    }

    override suspend fun deleteAllTareasForUser(userId: String): Boolean {
        tareaDao.deleteAllTareasForUser(userId)
        return true
    }
}