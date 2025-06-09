package com.example.summarynews.ui.ajustes

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentAjustesBinding

/**
 * [Fragment] que permite al usuario configurar diferentes ajustes de la aplicación,
 * como el modo oscuro y el idioma de la aplicación.
 *
 * Guarda los ajuste del usuario utilizando [SharedPreferences].
 */
class AjustesFragment : Fragment() {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_APP_SETTINGS = "AjustesAppSummaryNews"
    private val PREF_KEY_DARK_MODE = "dark_mode_enabled"

    /**
     * Se llama para que el fragmento instancie su jerarquía de vistas.
     *
     * Infla el layout `FragmentAjustesBinding` utilizando View Binding.
     *
     * @param inflater El LayoutInflater objeto que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la UI del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado
     * guardado anteriormente.
     * @return La [View] para la UI del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Se llama inmediatamente después de que [onCreateView] haya devuelto un valor,
     * pero antes de que cualquier estado guardado haya sido restaurado en la vista.
     *
     * Inicializa las [SharedPreferences] y configura los listeners para
     * el modo oscuro y el Spinner de idioma.
     *
     * @param view La [View] devuelta por [onCreateView].
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido a partir de un estado guardado previamente.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(PREF_APP_SETTINGS, Context.MODE_PRIVATE)

        configuracionModoOscuro()

        // Configurar Spinner para el idioma de la aplicación
        configuracionSpinner()

    }

    /**
     * Configura el comportamiento y el estado inicial del Switch para el modo oscuro.
     *
     * Carga el estado guardado del modo oscuro de las preferencias, lo aplica al Switch
     * y establece un listener para guardar el nuevo estado y aplicar el tema cuando
     * el usuario lo cambia.
     */
    private fun configuracionModoOscuro() {
        val isDarkModeEnabled =
            sharedPreferences.getBoolean(PREF_KEY_DARK_MODE, isSystemInDarkMode())
        binding.switchModoOscuro.isChecked = isDarkModeEnabled

        binding.switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit {
                putBoolean(PREF_KEY_DARK_MODE, isChecked)
            }
            applyTheme(isChecked)
            // Android recreará la actividad automáticamente al cambiar el modo oscuro.
        }
    }

    /**
     * Configura el Spinner para la selección de idioma.
     *
     * Crea un adaptador con las opciones definidas en `strings.xml`, carga la selección
     * guardada previamente en las preferencias y establece un listener para guardar
     * la nueva selección del usuario.
     */
    private fun configuracionSpinner() {
        val spinner: Spinner = binding.spinnerOpciones

        // Crear adaptador usando el array definido en strings.xml
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.opciones_spinner,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Cargar la selección guardada en SharedPreferences
        val prefKeySpinner = "spinner_selection"
        val savedSelection = sharedPreferences.getInt(prefKeySpinner, 0)
        spinner.setSelection(savedSelection)

        // Listener para guardar y reaccionar a la selección
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // Guardar la selección
                sharedPreferences.edit {
                    putInt(prefKeySpinner, position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }
    }

    /**
     * Aplica el tema de la aplicación (modo oscuro o claro).
     *
     * Utiliza [AppCompatDelegate.setDefaultNightMode] para cambiar globalmente
     * el modo oscuro de la aplicación.
     *
     * @param isDarkMode `true` para habilitar el modo oscuro, `false` para deshabilitarlo (modo claro).
     */
    private fun applyTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    /**
     * Comprueba si el sistema operativo está actualmente en modo oscuro.
     *
     * Utiliza la configuración de recursos para determinar el modo UI actual del dispositivo.
     *
     * @return `true` si el sistema está en modo oscuro, `false` en caso contrario.
     */
    private fun isSystemInDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
