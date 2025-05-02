package com.example.summarynews.ui.noticias

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.newsAPI.NewsRepository
import com.example.summarynews.newsAPI.Resource
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoticiasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(AppDatabase.getDatabase(application))
    private val _headlines = MutableLiveData<Resource<List<NoticiaEntity>>>()
    val headlines: LiveData<Resource<List<NoticiaEntity>>> = _headlines

    val guardadas: LiveData<List<NoticiaEntity>> = repository.getSavedNews()
    val todasLasNoticiasLocal: LiveData<List<NoticiaEntity>> = repository.getAllNews()
    val noticias: LiveData<List<NoticiaEntity>> = repository.getAllNews()

    private var pagina = 1


    fun cargarNuevasNoticias(countryCode: String, idUsuario: Int, onComplete: () -> Unit) {
        Log.d("NoticiasViewModel", "Cargando nuevas noticias desde la API (botón)")
        _headlines.value = Resource.Loading()

        viewModelScope.launch {
            repository.getHeadlines(countryCode, pagina, idUsuario).collectLatest { response ->
                _headlines.value = response
                if (response is Resource.Success) {
                    pagina++
                    onComplete()
                } else if (response is Resource.Error) {
                    onComplete()
                }
                // No llamamos a onComplete() si es Resource.Loading
            }
        }
    }


    fun getHeadlines(countryCode: String, idUsuario: Int) {
        viewModelScope.launch {
            repository.getHeadlines(countryCode, 1, idUsuario).collectLatest { response ->
                _headlines.value = response
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            repository.searchNews(query, 1).collectLatest { response ->
                _headlines.value = response // Podrías tener un LiveData separado para los resultados de búsqueda
            }
        }
    }

    fun actualizarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        repository.updateNews(noticia)
    }

    fun eliminarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        repository.deleteNews(noticia)
    }

    suspend fun noticiasLength(usuarioId: Int): Int {
        return AppDatabase.getDatabase(getApplication()).noticiaDao().contarNoticiasPorUsuario(usuarioId)
    }
}