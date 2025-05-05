package com.example.summarynews.ui.registro

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.summarynews.R
import com.example.summarynews.db.UsuarioEntity

class RegistroFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var nombreEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var logInButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registro, container, false)

        nombreEditText = view.findViewById(R.id.editTextNombre)
        emailEditText = view.findViewById(R.id.editTextEmail)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        registerButton = view.findViewById(R.id.btnRegistrar)
        logInButton = view.findViewById<Button>(R.id.btnIrALogin)

        registerButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, rellena todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nombre.length < 6) {
                Toast.makeText(requireContext(), "El nombre debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
            if (!email.matches(emailRegex)) {
                Toast.makeText(requireContext(), "Introduce un correo electrónico válido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.insertarUsuario(nombre, email, password)
        }

        logInButton.setOnClickListener {
            findNavController().navigate(R.id.action_registroFragment_to_loginFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginResult.observe(viewLifecycleOwner, Observer { registerResult ->
            when (registerResult) {
                is LoginResult.Success -> {
                    guardarSesion(registerResult.usuario)
                    // Ahora la MainActivity observa navigateToInicio
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), registerResult.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Observa el evento de navegación a InicioFragment
        viewModel.navigateToInicio.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(R.id.action_registroFragment_to_inicioFragment)
                viewModel.resetNavigateToInicio()
            }
        })
    }

    private fun guardarSesion(usuario: UsuarioEntity) {
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("email", usuario.email)
            putInt("userId", usuario.id)
            putString("nombreUsuario", usuario.nombre)
            apply()
        }
        Log.i("GUARDAR_SESION", "Guardado: Email=${usuario.email}, Nombre=${usuario.nombre}, ID=${usuario.id}")
    }
}