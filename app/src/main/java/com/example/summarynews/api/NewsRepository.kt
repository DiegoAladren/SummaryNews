package com.example.summarynews.api

import androidx.lifecycle.LiveData
import com.example.summarynews.R
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

class NewsRepository(db: AppDatabase) {

    private val newsAPI = RetrofitInstance.api
    private val noticiaDao = db.noticiaDao()

    fun getHeadlines(countryCode: String, pageNumber: Int, idUsuario: Int): Flow<Resource<List<NoticiaEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsAPI.getHeadlines(countryCode = countryCode, pageNumber = pageNumber)
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    val noticiasEntity = newsResponse.articles.map { article ->
                        NoticiaEntity(
                            titulo = article.title.toString(),
                            resumen = article.description.toString(),
                            imagenID = R.drawable.noticia1imagen,
                            imagenURL = article.urlToImage,
                            fuenteURL = article.url.toString(),
                            categoria = "", // La API no tiene una categoría clara a este nivel
                            liked = false,
                            saved = false,
                            usuarioId = idUsuario
                        )
                    }
                    noticiaDao.insertarNoticias(noticiasEntity) // Guarda las noticias en la base de datos
                    emit(Resource.Success(noticiasEntity))
                }
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de red o conversión de datos"))
        }
    }.flowOn(Dispatchers.IO)

    fun searchNews(searchQuery: String, pageNumber: Int): Flow<Resource<List<NoticiaEntity>>> = flow {
        emit(Resource.Loading())
        try {
            val response = newsAPI.searchForNews(searchQuery = searchQuery, pageNumber = pageNumber)
            if (response.isSuccessful) {
                response.body()?.let { newsResponse ->
                    val noticiasEntity = newsResponse.articles.map { article ->
                        NoticiaEntity(
                            titulo = article.title.toString(),
                            resumen = article.description.toString(),
                            imagenID = R.drawable.noticia1imagen,
                            imagenURL = article.urlToImage,
                            fuenteURL = article.url.toString(),
                            categoria = "", // Se podría intentar inferir la categoría de la búsqueda
                            liked = false,
                            saved = false,
                            usuarioId = 1
                        )
                    }
                    noticiaDao.insertarNoticias(noticiasEntity) // Guarda las noticias en la base de datos
                    emit(Resource.Success(noticiasEntity))
                }
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error de red o conversión de datos"))
        }
    }.flowOn(Dispatchers.IO)


    // Función para obtener todas las noticias guardadas localmente
    fun getSavedNews(): LiveData<List<NoticiaEntity>> = noticiaDao.getGuardadasLive()

    // Función para actualizar una noticia (liked, saved, etc.)
    suspend fun updateNews(noticia: NoticiaEntity) {
        noticiaDao.actualizarNoticia(noticia)
    }

    // Función para eliminar una noticia
    suspend fun deleteNews(noticia: NoticiaEntity) {
        noticiaDao.eliminarNoticia(noticia)
    }

    // Función para obtener todas las noticias de la base de datos (LiveData)
    fun getAllNews(): LiveData<List<NoticiaEntity>> = noticiaDao.getTodasLive()

    // Nueva función para verificar si hay noticias en la base de datos
    suspend fun isNewsDatabaseEmpty(): Boolean {
        return noticiaDao.contarNoticias() == 0
    }
}