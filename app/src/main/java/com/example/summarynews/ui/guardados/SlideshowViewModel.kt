package com.example.summarynews.ui.guardados

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Esto es el Fragment de Guardados"
    }
    val text: LiveData<String> = _text
}