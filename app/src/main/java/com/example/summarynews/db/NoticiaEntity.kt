package com.example.summarynews.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "noticias",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE // Si se elimina un usuario, se eliminan sus noticias
    )]
)
data class NoticiaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int, // Nuevo campo para el ID del usuario
    val titulo: String,
    val resumen: String,
    val imagenID: Int,
    val fuenteURL: String,
    val categoria: String,
    var liked: Boolean,
    var saved: Boolean,
)