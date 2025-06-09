package com.example.summarynews.ui.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.UsuarioEntity
import kotlinx.coroutines.launch

/**
 * [AndroidViewModel] que gestiona la lógica para las operaciones de inicio de sesión
 * y registro de usuarios.
 *
 * Interactúa con la base de datos a través de UsuarioDao para autenticar y registrar usuarios.
 * Proporciona [LiveData] para observar los resultados de estas operaciones ([loginResult])
 * y para desencadenar la navegación a la pantalla de inicio ([navigateToInicio]).
 *
 * @param application La instancia de [Application] de la aplicación.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    // Las variables con _ delante son privadas y mutables, las variables sin _ son públicas y solo de lectura
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _navigateToInicio = MutableLiveData<Boolean>()
    val navigateToInicio: LiveData<Boolean> = _navigateToInicio

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     *
     * Realiza validaciones básicas de los campos y luego lanza una corrutina
     * para consultar la base de datos. Actualiza [_loginResult] con el éxito
     * o el error de la operación y, en caso de éxito, establece [_navigateToInicio] en `true`.
     *
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("Por favor, rellena todos los campos.")
            return
        }

        viewModelScope.launch {
            try {
                val usuario = usuarioDao.login(email, password)
                if (usuario != null) {
                    // Obtener el usuario con ID completo (por si acaso)
                    val usuarioCompleto = usuarioDao.getById(usuario.id) ?: usuario
                    _loginResult.value = LoginResult.Success(usuarioCompleto)
                    _navigateToInicio.value = true
                } else {
                    _loginResult.value = LoginResult.Error("Credenciales incorrectas.")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    /**
     * Intenta registrar un nuevo usuario con la información proporcionada.
     *
     * Realiza validaciones de los campos, comprueba si el correo electrónico ya existe
     * en la base de datos y, si todo es válido, inserta el nuevo usuario.
     * Actualiza [_loginResult] con el éxito o el error de la operación y, en caso de éxito,
     * establece [_navigateToInicio] en `true`.
     *
     * @param nombre El nombre del nuevo usuario.
     * @param email El correo electrónico del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     */
    fun insertarUsuario(nombre: String, email: String, password: String) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            _loginResult.value = LoginResult.Error("Por favor, rellena todos los campos para registrarte.")
            return
        }

        viewModelScope.launch {
            try {
                // Verificar si el email ya existe
                if (usuarioDao.getByEmail(email) != null) {
                    _loginResult.value = LoginResult.Error("Este email ya está registrado")
                    return@launch
                }

                val nuevoUsuario = UsuarioEntity(
                    nombre = nombre,
                    email = email,
                    password = password
                )

                // Insertar y obtener el ID generado
                val userId = usuarioDao.insertar(nuevoUsuario).toInt()

                // Obtener el usuario completo con su ID
                val usuarioInsertado = usuarioDao.getById(userId) ?: throw Exception("Usuario no encontrado después de insertar")

                _loginResult.value = LoginResult.Success(usuarioInsertado)
                _navigateToInicio.value = true
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Error al registrar: ${e.message}")
            }
        }
    }

    /**
     * Restablece el estado de navegación a la pantalla de inicio.
     *
     * Esto asegura que el evento de navegación solo se desencadene una vez
     * después de un inicio de sesión o registro exitoso.
     */
    fun resetNavigateToInicio() {
        _navigateToInicio.value = false
    }
}

/**
 * Clase `sealed` que representa el resultado de una operación de inicio de sesión o registro.
 *
 * Permite modelar explícitamente los posibles estados de una operación asíncrona:
 * éxito con los datos del usuario o un error con un mensaje descriptivo.
 */
sealed class LoginResult {
    data class Success(val usuario: UsuarioEntity) : LoginResult()
    data class Error(val message: String) : LoginResult()
}