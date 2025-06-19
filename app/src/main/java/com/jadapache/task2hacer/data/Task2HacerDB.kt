package com.jadapache.task2hacer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.jadapache.task2hacer.data.models.Tarea
import com.jadapache.task2hacer.data.daos.TareaDao
import com.jadapache.task2hacer.data.models.Usuario
import com.jadapache.task2hacer.data.daos.UsuarioDao


@Database(entities = [Tarea::class, Usuario::class], version = 1, exportSchema = false)
abstract class Task2HacerDB: RoomDatabase() {

    abstract fun getTareaDao(): TareaDao
    abstract fun getUsuarioDao(): UsuarioDao


    companion object {
        @Volatile
        private var instance: Task2HacerDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK) {
            instance ?:
            createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            databaseBuilder(
                context.applicationContext,
                Task2HacerDB::class.java,
                "note_db"
            ).build()
    }

}