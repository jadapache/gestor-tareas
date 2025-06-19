package com.jadapache.task2hacer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    val id: String,
    val email: String,
    val pass: String,
    var ultimaActualizacion: Long? = null
) 