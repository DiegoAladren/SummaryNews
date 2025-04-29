package com.example.summarynews.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoticiaDao {

    @Query("SELECT * FROM noticias WHERE userId = :userId")
    fun getTodasLive(userId: Int): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias WHERE userId = :userId")
    suspend fun getTodas(userId: Int): List<NoticiaEntity>

    @Query("SELECT * FROM noticias WHERE userId = :userId AND saved = 1")
    fun getGuardadasLive(userId: Int): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias WHERE userId = :userId AND saved = 1")
    suspend fun getGuardadas(userId: Int): List<NoticiaEntity>

    @Query("SELECT * FROM noticias WHERE userId = :userId AND categoria = :categoria")
    suspend fun getPorCategoria(userId: Int, categoria: String): List<NoticiaEntity>

    @Delete
    suspend fun eliminarNoticia(noticia: NoticiaEntity)

    @Query("SELECT COUNT(*) FROM noticias WHERE userId = :userId")
    suspend fun contarNoticias(userId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNoticias(noticias: List<NoticiaEntity>)

    @Update
    suspend fun actualizarNoticia(noticia: NoticiaEntity)

    @Query("SELECT * FROM noticias WHERE userId = :userId")
    fun getNoticiasPorUsuario(userId: Int): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias WHERE userId = :userId AND saved = 1")
    fun getGuardadasPorUsuario(userId: Int): LiveData<List<NoticiaEntity>>

    @Query("SELECT COUNT(*) FROM noticias WHERE userId = :userId")
    suspend fun contarNoticiasPorUsuario(userId: Int): Int
}