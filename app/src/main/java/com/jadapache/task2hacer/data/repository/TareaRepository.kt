package com.jadapache.task2hacer.data.repository

import com.jadapache.task2hacer.data.daos.TareaDao
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.utils.encriptationUtil
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TareaRepository(private val tareaDao: TareaDao, private val context: Context) : ITareaRepository {

    override fun getAllTareas(): Flow<List<Tarea>> = tareaDao.getAll().map { tareas ->
        tareas.map { it.copy(descripcion = encriptationUtil.decrypt(context, it.descripcion)) }
    }
    override fun getAllTareasByUser(userId: String): Flow<List<Tarea>> = tareaDao.getAllTareasByUser(userId).map { tareas ->
        tareas.map { it.copy(descripcion = encriptationUtil.decrypt(context, it.descripcion)) }
    }

    override suspend fun insertTarea(tarea: Tarea): Boolean {
        val encryptedDesc = encriptationUtil.encrypt(context, tarea.descripcion)
        tareaDao.insert(tarea.copy(descripcion = encryptedDesc))
        return true
    }

    override suspend fun insertTareas(tasks: List<Tarea>): Boolean {
        val encryptedTasks = tasks.map { it.copy(descripcion = encriptationUtil.encrypt(context, it.descripcion)) }
        tareaDao.bulkInsert(encryptedTasks)
        return true
    }

    override suspend fun updateTarea(tarea: Tarea): Boolean {
        val encryptedDesc = encriptationUtil.encrypt(context, tarea.descripcion)
        tareaDao.update(tarea.copy(descripcion = encryptedDesc))
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