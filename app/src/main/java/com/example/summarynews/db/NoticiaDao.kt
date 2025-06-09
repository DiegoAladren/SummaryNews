package com.example.summarynews.db

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO (Data Access Object) para la entidad [NoticiaEntity].
 *
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * y otras consultas personalizadas sobre la tabla de noticias en la base de datos.
 * Las funciones `suspend` están diseñadas para ser llamadas desde corutinas.
 * Las funciones que devuelven `LiveData` permiten observar los cambios en los datos.
 */
@Dao
interface NoticiaDao {

    /**
     * Obtiene todas las noticias de la base de datos como un [LiveData].
     * Útil para observar cambios en tiempo real en la UI.
     * @return Un [LiveData] que contiene una lista de todas las [NoticiaEntity].
     */
    @Query("SELECT * FROM noticias ")
    fun getTodasLive(): LiveData<List<NoticiaEntity>>

    /**
     * Obtiene todas las noticias de la base de datos.
     * @return Una lista de todas las [NoticiaEntity].
     */
    @Query("SELECT * FROM noticias ")
    suspend fun getTodas(): List<NoticiaEntity>

    /**
     * Obtiene todas las noticias marcadas como guardadas (saved = 1) como un [LiveData].
     * @return Un [LiveData] que contiene una lista de [NoticiaEntity] guardadas.
     */
    @Query("SELECT * FROM noticias WHERE  saved = 1")
    fun getGuardadasLive(): LiveData<List<NoticiaEntity>>

    /**
     * Obtiene todas las noticias marcadas como guardadas (saved = 1).
     * @return Una lista de [NoticiaEntity] guardadas.
     */
    @Query("SELECT * FROM noticias WHERE  saved = 1")
    suspend fun getGuardadas(): List<NoticiaEntity>

    /**
     * Obtiene todas las noticias pertenecientes a una categoría específica.
     * @param categoria La categoría por la cual filtrar las noticias.
     * @return Una lista de [NoticiaEntity] que coinciden con la categoría especificada.
     */
    @Query("SELECT * FROM noticias WHERE  categoria = :categoria")
    suspend fun getPorCategoria(categoria: String): List<NoticiaEntity>

    /**
     * Elimina una noticia específica de la base de datos.
     * @param noticia La [NoticiaEntity] a eliminar.
     */
    @Delete
    suspend fun eliminarNoticia(noticia: NoticiaEntity)

    /**
     * Cuenta el número total de noticias en la base de datos.
     * @return El número total de noticias.
     */
    @Query("SELECT COUNT(*) FROM noticias ")
    suspend fun contarNoticias(): Int

    /**
     * Cuenta el número de noticias asociadas a un ID de usuario específico.
     * @param usuarioId El ID del usuario para el cual contar las noticias.
     * @return El número de noticias para el usuario especificado.
     */
    @Query("SELECT COUNT(*) FROM noticias WHERE usuarioId = :usuarioId")
    suspend fun contarNoticiasPorUsuario(usuarioId: Int): Int

    /**
     * Inserta una lista de noticias en la base de datos.
     * Si una noticia ya existe (basado en su clave primaria), será reemplazada.
     * @param noticias La lista de [NoticiaEntity] a insertar o reemplazar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNoticias(noticias: List<NoticiaEntity>)

    /**
     * Actualiza una noticia existente en la base de datos.
     * @param noticia La [NoticiaEntity] con los datos actualizados.
     */
    @Update
    suspend fun actualizarNoticia(noticia: NoticiaEntity)

}