package com.example.summarynews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [NoticiaEntity::class, UsuarioEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noticiaDao(): NoticiaDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noticias_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
