package com.example.summarynews.ui.noticias

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoticiasViewModelFactory(
    private val application: Application,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticiasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticiasViewModel(application, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}