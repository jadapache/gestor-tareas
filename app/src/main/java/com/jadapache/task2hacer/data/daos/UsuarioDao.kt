package com.jadapache.task2hacer.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.jadapache.task2hacer.data.models.Usuario

@Dao
interface UsuarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id = :uid")
    suspend fun getUserById(uid: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :uid")
    fun observeUserById(uid: String): Flow<Usuario?>

    @Query("DELETE FROM usuarios WHERE id = :uid")
    suspend fun deleteUser(uid: String)


} 