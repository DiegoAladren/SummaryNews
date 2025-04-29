package com.example.summarynews.ui.noticias

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import kotlinx.coroutines.launch

class NoticiasViewModel(application: Application, private val userId: Int) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).noticiaDao()

    init {
        // Verificación importante
        if (userId <= 0) {
            throw IllegalArgumentException("ID de usuario inválido: $userId")
        }
    }

    val noticias: LiveData<List<NoticiaEntity>> = dao.getNoticiasPorUsuario(userId)

    // Noticias guardadas filtradas por userId
    val guardadas: LiveData<List<NoticiaEntity>> = liveData {
        emitSource(dao.getGuardadasPorUsuario(userId))
    }

    fun insertarNoticias(noticias: List<NoticiaEntity>) = viewModelScope.launch {
        // Forzar el userId correcto en todas las noticias
        val noticiasConUsuario = noticias.map {
            it.copy(userId = userId)
        }
        dao.insertarNoticias(noticiasConUsuario)
    }

    fun actualizarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        // Verificar que la noticia pertenece al usuario actual
        if (noticia.userId == userId) {
            dao.actualizarNoticia(noticia)
        }
    }

    suspend fun noticiasLength(): Int {
        return dao.contarNoticiasPorUsuario(userId)
    }
}