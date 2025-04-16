package com.example.summarynews.ui.noticias

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.NoticiaEntity
import kotlinx.coroutines.launch

class NoticiasViewModel (application: Application) : AndroidViewModel(application) {

    // Instancia del dao para acceder a la base de datos
    private val dao = AppDatabase.getDatabase(application).noticiaDao()


    // LiveData que observa automáticamente los datos desde la base de datos
    val noticias: LiveData<List<NoticiaEntity>> = liveData {
        emitSource(dao.getTodasLive()) // Este método tiene que devolver LiveData desde el DAO
    }

    val guardadas: LiveData<List<NoticiaEntity>> = liveData {
        emitSource(dao.getGuardadasLive()) // Este también
    }

    // Insertar noticias en la base de datos (por ejemplo tras obtenerlas desde la API)
    fun insertarNoticias(noticias: List<NoticiaEntity>) = viewModelScope.launch {
        dao.insertarNoticias(noticias)
    }

    // Actualizar una noticia (por ejemplo cuando el usuario da "guardar" o "like")
    fun actualizarNoticia(noticia: NoticiaEntity) = viewModelScope.launch {
        dao.actualizarNoticia(noticia)
    }

    // Obtener el número de noticias actuales (forma suspendida)
    suspend fun noticiasLength(): Int {
        return dao.contarNoticias()
    }
}
