package com.example.summarynews.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "noticias",
)
data class NoticiaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val resumen: String,
    val imagenID: Int,
    val imagenURL: String?,
    val fuenteURL: String,
    val categoria: String,
    var liked: Boolean,
    var saved: Boolean,
    val usuarioId: Int
)