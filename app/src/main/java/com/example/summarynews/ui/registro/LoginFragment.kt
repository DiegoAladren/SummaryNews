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

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        emailEditText = view.findViewById(R.id.editTextEmail)
        passwordEditText = view.findViewById(R.id.editTextPassword)
        loginButton = view.findViewById(R.id.btnLogin)
        registerButton = view.findViewById(R.id.btnIrARegistrar)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            viewModel.login(email, password)
        }

        registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registroFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loginResult.observe(viewLifecycleOwner, Observer { loginResult ->
            when (loginResult) {
                is LoginResult.Success -> {
                    guardarSesion(loginResult.usuario)
                    // Ahora la MainActivity observa navigateToInicio
                }
                is LoginResult.Error -> {
                    Toast.makeText(requireContext(), loginResult.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        // Observa el evento de navegación a InicioFragment
        viewModel.navigateToInicio.observe(viewLifecycleOwner, Observer { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(R.id.action_loginFragment_to_inicioFragment)
                viewModel.resetNavigateToInicio()
            }
        })
    }

    private fun guardarSesion(usuario: UsuarioEntity) {  // Cambia el parámetro a UsuarioEntity
        Log.i("LoginFragment", "Guardando sesión para el usuario (paso yo antes): ${usuario.email}")
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("email", usuario.email)
            putInt("userId", usuario.id)  // Guarda el ID del usuario
            apply()
        }
    }
}