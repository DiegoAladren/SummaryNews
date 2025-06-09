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

/**
 * [Fragment] encargado de la interfaz de usuario para el inicio de sesión.
 *
 * Este fragmento permite a los usuarios introducir sus credenciales (correo electrónico y contraseña)
 * para iniciar sesión. Interactúa con un [LoginViewModel]
 * para manejar la lógica de autenticación y la navegación.
 */
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    /**
     * Es llamado para que el fragmento instancie su jerarquía de vistas.
     *
     * Infla el layout `fragment_login.xml` y obtiene las referencias a los elementos de la UI.
     * También configura los listeners para los botones de inicio de sesión y registro.
     *
     * @param inflater El [LayoutInflater] que puede usarse para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista padre a la que se debe adjuntar la UI del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido a partir de un estado guardado previamente.
     * @return La [View] para la UI del fragmento.
     */
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

    /**
     * Es llamado inmediatamente después de que [onCreateView] haya devuelto un valor,
     * pero antes de que cualquier estado guardado haya sido restaurado en la vista.
     *
     * Observa los resultados del inicio de sesión ([LoginResult]) y los eventos de navegación
     * desde el [LoginViewModel].
     *
     * @param view La [View] devuelta por [onCreateView].
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido a partir de un estado guardado previamente.
     */
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

    /**
     * Guarda la información del usuario en SharedPreferences para mantener la sesión.
     *
     * Almacena el email, el ID de usuario y el nombre del usuario logueado.
     *
     * @param usuario La [UsuarioEntity] que contiene los datos del usuario logueado.
     */
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