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

class AjustesFragment : Fragment() {

    private var _binding: FragmentAjustesBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val PREF_APP_SETTINGS = "AjustesAppSummaryNews"
    private val PREF_KEY_DARK_MODE = "dark_mode_enabled"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAjustesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(PREF_APP_SETTINGS, Context.MODE_PRIVATE)

        val isDarkModeEnabled = sharedPreferences.getBoolean(PREF_KEY_DARK_MODE, isSystemInDarkMode())
        binding.switchModoOscuro.isChecked = isDarkModeEnabled

        binding.switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit {
                putBoolean(PREF_KEY_DARK_MODE, isChecked)
            }
            applyTheme(isChecked)
            // Android recreará la actividad automáticamente al cambiar el modo oscuro.
        }
        // Configurar Spinner
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
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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

    private fun applyTheme(isDarkMode: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun isSystemInDarkMode(): Boolean {
        return (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
