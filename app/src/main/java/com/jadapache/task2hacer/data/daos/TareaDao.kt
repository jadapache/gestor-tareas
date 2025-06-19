package com.jadapache.task2hacer.data.daos

import androidx.room.*
import com.jadapache.task2hacer.data.models.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarea: Tarea)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun bulkInsert(tasks: List<Tarea>)

    @Update
    suspend fun update(tarea: Tarea)

    @Delete
    suspend fun deleteTarea(task: Tarea)

    @Delete
    suspend fun bulkDelete(tasks: List<Tarea>)

    @Query("SELECT * FROM tareas ORDER BY ultimaMod DESC")
    fun getAll(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas where userId = :userId ORDER BY ultimaMod DESC")
    fun getAllTareasByUser(userId: String): Flow<List<Tarea>>

    @Query("DELETE FROM tareas WHERE userId = :userId")
    suspend fun deleteAllTareasForUser(userId: String)

} 