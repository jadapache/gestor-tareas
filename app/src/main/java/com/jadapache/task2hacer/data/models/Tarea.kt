package com.jadapache.task2hacer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var userId: String = "",
    var nombre: String = "",
    var descripcion: String = "",
    val completada: Boolean = false,
    var sincronizado: Boolean = false,
    var ultimaMod: Long = System.currentTimeMillis()
)
