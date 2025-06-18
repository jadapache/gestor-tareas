package com.jadapache.task2hacer.data.repositories

import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.data.daos.TareaDao
import kotlinx.coroutines.flow.Flow

class TareaRepository(private val tareaDao: TareaDao) {
    fun getAllTareas(): Flow<List<Tarea>> = tareaDao.getAll()
    suspend fun insertTarea(tarea: Tarea): Long = tareaDao.insert(tarea)
    suspend fun updateTarea(tarea: Tarea) = tareaDao.update(tarea)
    suspend fun deleteTarea(tarea: Tarea) = tareaDao.delete(tarea)
    suspend fun getTareaById(id: Int): Tarea? = tareaDao.getById(id)
} 