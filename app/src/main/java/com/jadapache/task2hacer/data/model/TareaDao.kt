package com.jadapache.task2hacer.data.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun getAll(): Flow<List<Tarea>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarea: Tarea): Long

    @Update
    suspend fun update(tarea: Tarea)

    @Delete
    suspend fun delete(tarea: Tarea)

    @Query("SELECT * FROM tareas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Tarea?
} 