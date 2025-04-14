package com.example.summarynews.ui.noticias

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.summarynews.ui.noticias.Noticia

class NoticiasViewModel : ViewModel() {

    private val _noticias = MutableLiveData<List<Noticia>>(emptyList())
    val noticias: LiveData<List<Noticia>> get() = _noticias

    fun setNoticias(lista: List<Noticia>) {
        _noticias.value = lista
    }

    fun getNoticias(): List<Noticia>? {
        return _noticias.value
    }

    fun actualizarNoticia(noticiaActualizada: Noticia) {
        _noticias.value = _noticias.value?.map {
            if (it.titulo == noticiaActualizada.titulo) noticiaActualizada else it
        }
    }

    fun getGuardadas(): List<Noticia> {
        return _noticias.value?.filter { it.saved } ?: emptyList()
    }

    fun noticiasLength(): Int {

        return _noticias.value?.size ?: 0
    }
}
