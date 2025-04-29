package com.example.summarynews.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NoticiaDao {

    @Query("SELECT * FROM noticias ")
    fun getTodasLive(): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias ")
    suspend fun getTodas(): List<NoticiaEntity>

    @Query("SELECT * FROM noticias WHERE  saved = 1")
    fun getGuardadasLive(): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias WHERE  saved = 1")
    suspend fun getGuardadas(): List<NoticiaEntity>

    @Query("SELECT * FROM noticias WHERE  categoria = :categoria")
    suspend fun getPorCategoria(categoria: String): List<NoticiaEntity>

    @Delete
    suspend fun eliminarNoticia(noticia: NoticiaEntity)

    @Query("SELECT COUNT(*) FROM noticias ")
    suspend fun contarNoticias(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNoticias(noticias: List<NoticiaEntity>)

    @Update
    suspend fun actualizarNoticia(noticia: NoticiaEntity)

    @Query("SELECT * FROM noticias ")
    fun getNoticiasPorUsuario(): LiveData<List<NoticiaEntity>>

    @Query("SELECT * FROM noticias WHERE saved = 1")
    fun getGuardadasPorUsuario(): LiveData<List<NoticiaEntity>>

    @Query("SELECT COUNT(*) FROM noticias ")
    suspend fun contarNoticiasPorUsuario(): Int
}