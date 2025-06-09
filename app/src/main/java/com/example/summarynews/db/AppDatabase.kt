package com.example.summarynews.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase principal de la base de datos Room para la aplicación.
 *
 * Esta clase abstracta extiende [RoomDatabase] y sirve como el punto de acceso principal
 * a la base de datos de la aplicación. Define las entidades que componen la
 * base de datos y proporciona acceso a los DAOs (Data Access Objects) para interactuar
 * con esas entidades.
 *
 * La base de datos utiliza un patrón Singleton para asegurar que solo exista una instancia
 * de la base de datos en toda la aplicación.
 *
 * @property entities Lista de clases de entidad que forman parte de esta base de datos.
 * @property version Número de versión de la base de datos. Debe incrementarse al realizar cambios en el esquema.
 */
@Suppress("KDocUnresolvedReference")
@Database(entities = [NoticiaEntity::class, UsuarioEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Proporciona una instancia del DAO para las operaciones de la entidad [NoticiaEntity].
     * @return Una instancia de [NoticiaDao].
     */
    abstract fun noticiaDao(): NoticiaDao

    /**
     * Proporciona una instancia del DAO para las operaciones de la entidad [UsuarioEntity].
     * @return Una instancia de [UsuarioDao].
     */
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        /**
         * Instancia Singleton de [AppDatabase].
         *
         * La anotación `@Volatile` asegura que los cambios en esta variable sean visibles
         * inmediatamente para todos los hilos.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Obtiene la instancia Singleton de la base de datos.
         *
         * Si la instancia ya existe, la devuelve. Si no, crea una nueva instancia de la base de datos
         * de forma y la devuelve.
         *
         * @param context El contexto de la aplicación, usado para obtener la ruta del archivo de la base de datos.
         * @return La instancia Singleton de [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            // Si INSTANCE no es nula, la devuelve; si es nula, crea la base de datos.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noticias_db" // Nombre de la base de datos
                )
                    .fallbackToDestructiveMigration(false) // Se utiliza para permitir migraciones destructivas (esto es solo se mantendrá durante el desarrollo)
                    .build()
                INSTANCE = instance
                // Devuelve la instancia
                instance
            }
        }
    }
}