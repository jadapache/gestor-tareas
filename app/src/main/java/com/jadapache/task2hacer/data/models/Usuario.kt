package com.jadapache.task2hacer.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey
    val id: String = "",
    val email: String = "",
    var fullname: String = "",
    var ultimaMod: Long = System.currentTimeMillis()
) 