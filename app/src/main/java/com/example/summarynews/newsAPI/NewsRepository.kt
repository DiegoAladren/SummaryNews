package com.example.summarynews.newsAPI

import androidx.lifecycle.LiveData
import com.example.summarynews.R
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import com.example.summarynews.geminiAPI.Content
import com.example.summarynews.geminiAPI.GeminiRequest
import com.example.summarynews.geminiAPI.GeminiService
import com.example.summarynews.geminiAPI.Part
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
                    val noticiasEntity = withContext(Dispatchers.IO) {
                        newsResponse.articles.mapNotNull { article ->
                            try {
                                val (titulo, resumen, categoria) = procesarArticuloGemini(
                                    article.title,
                                    article.description
                                )
                                NoticiaEntity(
                                    titulo = titulo,
                                    resumen = resumen,
                                    imagenID = R.drawable.noticia1imagen,
                                    imagenURL = article.urlToImage,
                                    fuenteURL = article.url.toString(),
                                    categoria = categoria,
                                    liked = false,
                                    saved = false,
                                    usuarioId = idUsuario
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null // Si falla Gemini, ignora esa noticia
                            }
                        }
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
                            imagenID = R.drawable.placeholder_image,
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

    suspend fun procesarArticuloGemini(titulo: String?, descripcion: String?): Triple<String, String, String> {
        val prompt = """
        Título: ${titulo ?: ""}
        Descripción: ${descripcion ?: ""}
        
        Traduce ambos al español. Haz que el título tenga un máximo de 75 caracteres y mínimo 50 y la descripción entre 40 y 50 palabras. 
        Además, sugiere una categoría general que sea "Política, Deportes, Tecnología, Salud, Economía, Ciencia, Cultura, Opinión. Solo puede ser una de esas y tiene que estar así escrito
        Devuélvelo en formato JSON así:
        {
          "titulo": "...",
          "resumen": "...",
          "categoria": "..."
        }
    """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt))))
        )

        val response = withContext(Dispatchers.IO) {
            GeminiService.getApi().generarContenido("AIzaSyAkScetK6hNBDyd5FBkAjQz7wRAMGV_oN4", request).execute()
        }

        if (response.isSuccessful) {
            val resultText = response.body()?.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text ?: throw Exception("Sin respuesta")

            val cleanJson = resultText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanJson)

            return Triple(
                json.getString("titulo"),
                json.getString("resumen"),
                json.getString("categoria")
            )

        } else {
            throw Exception("Error de Gemini: ${response.code()}")
        }
    }

}