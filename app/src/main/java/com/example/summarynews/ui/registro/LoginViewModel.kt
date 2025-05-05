package com.example.summarynews.ui.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.summarynews.db.AppDatabase
import com.example.summarynews.db.UsuarioEntity
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

    // Las variables con _ delante son privadas y mutables, las variables sin _ son públicas y solo de lectura
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _navigateToInicio = MutableLiveData<Boolean>()
    val navigateToInicio: LiveData<Boolean> = _navigateToInicio

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

    fun resetNavigateToInicio() {
        _navigateToInicio.value = false
    }
}

sealed class LoginResult {
    data class Success(val usuario: UsuarioEntity) : LoginResult()
    data class Error(val message: String) : LoginResult()
}