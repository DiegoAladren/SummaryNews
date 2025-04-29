package com.example.summarynews.ui.noticias

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import kotlinx.coroutines.launch

class NoticiasViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).noticiaDao()


    val noticias: LiveData<List<NoticiaEntity>> = dao.getNoticiasPorUsuario()

    // Noticias guardadas filtradas por userId
    val guardadas: LiveData<List<NoticiaEntity>> = liveData {
        emitSource(dao.getGuardadasPorUsuario())
    }

    fun insertarNoticias(noticias: List<NoticiaEntity>) = viewModelScope.launch {

        dao.insertarNoticias(noticias)
    }

    fun actualizarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        // Verificar que la noticia pertenece al usuario actual
        dao.actualizarNoticia(noticia)
    }

    suspend fun noticiasLength(): Int {
        return dao.contarNoticiasPorUsuario()
    }
}