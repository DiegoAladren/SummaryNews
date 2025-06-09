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

/**
 * [NoticiasViewModel] es un [AndroidViewModel] que gestiona la lógica y la interacción con los datos
 * para la pantalla de noticias. Se encarga de cargar noticias de la API, gestionar las noticias guardadas
 * y proporcionar acceso a los datos de noticias a la UI.
 *
 * @property application La instancia de la aplicación.
 */
class NoticiasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NewsRepository(AppDatabase.getDatabase(application))
    private val _headlines = MutableLiveData<Resource<List<NoticiaEntity>>>()

    val guardadas: LiveData<List<NoticiaEntity>> = repository.getSavedNews()
    val todasLasNoticiasLocal: LiveData<List<NoticiaEntity>> = repository.getAllNews()
    val noticias: LiveData<List<NoticiaEntity>> = repository.getAllNews()

    private var pagina = 1


    /**
     * Carga nuevas noticias desde la API de noticias.
     * Actualiza [_headlines] con el estado de la operación (cargando, éxito o error).
     * Si la carga es exitosa, incrementa la página para la próxima solicitud.
     *
     * @param countryCode El código de país para filtrar las noticias (ej. "us" para Estados Unidos).
     * @param idUsuario El ID del usuario asociado a la solicitud.
     * @param onComplete Una función lambda que se ejecuta cuando la operación de carga de noticias se completa.
     * No se llama si el estado es [Resource.Loading].
     */
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
                // No llama a onComplete() si es Resource.Loading para que se siga mostrando el mensaje de carga
            }
        }
    }


    /**
     * Actualiza una noticia existente en la base de datos.
     * Esta operación se lanza en el [viewModelScope] para ejecutarse en un hilo de fondo.
     *
     * @param noticia La [NoticiaEntity] a actualizar.
     */
    fun actualizarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        repository.updateNews(noticia)
    }

    /**
     * Elimina una noticia de la base de datos.
     * Esta operación se lanza en el [viewModelScope] para ejecutarse en un hilo de fondo.
     *
     * @param noticia La [NoticiaEntity] a eliminar.
     */
    fun eliminarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        repository.deleteNews(noticia)
    }

    /**
     * Obtiene el número total de noticias asociadas a un ID de usuario específico
     * desde la base de datos.
     *
     * @param usuarioId El ID del usuario cuyas noticias se quieren contar.
     * @return El número de noticias para el usuario dado.
     */
    suspend fun noticiasLength(usuarioId: Int): Int {
        return AppDatabase.getDatabase(getApplication()).noticiaDao().contarNoticiasPorUsuario(usuarioId)
    }
}