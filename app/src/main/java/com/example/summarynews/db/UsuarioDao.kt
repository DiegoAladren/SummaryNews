package com.example.summarynews.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerPrimerUsuario(): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getByEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: Int): UsuarioEntity?
}