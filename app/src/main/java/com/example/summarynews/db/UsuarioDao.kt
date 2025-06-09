package com.example.summarynews.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO (Data Access Object) para la entidad [UsuarioEntity].
 *
 * Proporciona métodos para interactuar con la tabla de usuarios en la base de datos,
 * incluyendo inserción, y consultas para login y recuperación de usuarios.
 * Todas las funciones son de suspensión y deben ser llamadas desde corutinas.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     * @param usuario El [UsuarioEntity] a insertar.
     * @return El ID de la fila del usuario insertado.
     */
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    /**
     * COmprobar las credenciales de un usuario para el login.
     * Busca un usuario que coincida con el email y la contraseña proporcionados.
     * @param email El email del usuario.
     * @param password La contraseña del usuario.
     * @return El [UsuarioEntity] si las credenciales son correctas, o `null` en caso contrario.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): UsuarioEntity?

    /**
     * Obtiene el primer usuario encontrado en la tabla de usuarios.
     * Útil para pruebas donde solo se espera un usuario.
     * @return El primer [UsuarioEntity] encontrado, o `null` si la tabla está vacía.
     */
    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerPrimerUsuario(): UsuarioEntity?

    /**
     * Obtiene un usuario por su dirección de email.
     * @param email El email del usuario a buscar.
     * @return El [UsuarioEntity] correspondiente al email, o `null` si no se encuentra.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getByEmail(email: String): UsuarioEntity?

    /**
     * Obtiene un usuario por su ID.
     * @param id El ID único del usuario a buscar.
     * @return El [UsuarioEntity] correspondiente al ID, o `null` si no se encuentra.
     */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: Int): UsuarioEntity?
}