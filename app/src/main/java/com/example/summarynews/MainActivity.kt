package com.example.summarynews

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.summarynews.databinding.ActivityMainBinding
import com.example.summarynews.ui.inicio.InicioFragment
import com.google.android.material.tabs.TabLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.example.summarynews.ui.registro.LoginViewModel
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.example.summarynews.ui.noticias.NoticiasViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: androidx.navigation.NavController
    private lateinit var tabLayout: TabLayout
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener
    private val noticiasViewModel: NoticiasViewModel by viewModels()

    private val PREF_APP_SETTINGS = "AjustesAppSummaryNews"
    private val PREF_KEY_DARK_MODE = "dark_mode_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplicar el tema guardado ANTES de super.onCreate y setContentView
        val appSettingsPrefs = getSharedPreferences(PREF_APP_SETTINGS, MODE_PRIVATE)
        val isDarkModeEnabled = appSettingsPrefs.getBoolean(PREF_KEY_DARK_MODE, isSystemInDarkMode())
        applyAppTheme(isDarkModeEnabled)

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        tabLayout = findViewById(R.id.tab_layout)
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as androidx.navigation.fragment.NavHostFragment
        navController = navHostFragment.navController

        var estaCargando = false

        // Configurando el botón flotante que carga noticias al pulsarlo.
        binding.appBarMain.fab.setOnClickListener { view ->
            val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
            val idUsuario = sharedPref.getInt("userId", -1)

            if (estaCargando) return@setOnClickListener // Salir si ya está en carga

            estaCargando = true
            Log.i("MainActivity", "Cargando nuevas noticias desde el FAB"+estaCargando)
            binding.appBarMain.fab.isEnabled = false
            val snackbarCargando = Snackbar.make(view, "Cargando nuevas noticias...", Snackbar.LENGTH_INDEFINITE)
                .setAnchorView(R.id.fab)
            snackbarCargando.show()

            noticiasViewModel.cargarNuevasNoticias("us", idUsuario) {
                estaCargando = false
                Log.i("MainActivity", "Noticias cargadas correctamente"+estaCargando)
                binding.appBarMain.fab.isEnabled = true
                snackbarCargando.dismiss()

                Snackbar.make(view, "Noticias cargadas correctamente", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.fab)
                    .show()
            }

        }
        // Solo comprueba la sesión inicial y navega si la actividad se crea por primera vez
        // (savedInstanceState es null). Si no es null, el estado de navegación se restaurará
        // automáticamente por el NavController.
        if (savedInstanceState == null) {
            verificarSesionInicial()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_liked, R.id.nav_saved
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.nav_home)
                R.id.nav_liked -> navController.navigate(R.id.nav_liked)
                R.id.nav_saved -> navController.navigate(R.id.nav_saved)
                R.id.nav_logout -> cerrarSesion()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_home) {
                tabLayout.visibility = View.VISIBLE
                binding.appBarMain.fab.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                setupTabLayout()

                // Seleccionar por defecto el tab "Todas"
                val tabTodas = (0 until tabLayout.tabCount)
                    .map { tabLayout.getTabAt(it) }
                    .firstOrNull { it?.text == "Todas" }
                tabTodas?.select()

                // Forzar filtrado por categoría "Todas" cuando entras a InicioFragment
                val currentFragment = supportFragmentManager
                    .primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
                if (currentFragment is InicioFragment) {
                    currentFragment.filtrarPorCategoriaDesdeActivity("Todas")
                }

            } else {
                tabLayout.visibility = View.GONE
                binding.appBarMain.fab.visibility = View.GONE
                if (destination.id == R.id.loginFragment || destination.id == R.id.registroFragment || destination.id == R.id.nav_ajustes) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
            invalidateOptionsMenu()
        }

        loginViewModel.navigateToInicio.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(R.id.action_global_inicioFragment)
                loginViewModel.resetNavigateToInicio()
            }
        })

        // Inicializamos el listener para SharedPreferences
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "nombreUsuario" || key == "email") {
                actualizarHeaderNavigation()
            }
        }
    }

    private fun applyAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun isSystemInDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.action_global_to_ajustesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isEnAjustes = navController.currentDestination?.id == R.id.nav_ajustes
        menu.findItem(R.id.action_settings)?.isVisible = !isEnAjustes
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        // Registramos el listener para escuchar los cambios
        sharedPref.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        // Llamamos a actualizar al iniciar la actividad para mostrar los datos iniciales si hay sesión
        actualizarHeaderNavigation()
    }

    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        // Desregistramos el listener para evitar fugas de memoria
        sharedPref.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    // Si ya habías iniciado sesión no hace falta volver a hacerlo aunque cierres la aplicación
    // hasta que cierres sesión voluntariamente desde el menú.
    private fun verificarSesionInicial() {
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val emailGuardado = sharedPref.getString("email", null)

        if (emailGuardado == null) {
            navController.navigate(R.id.loginFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            // La actualización inicial se hará en onResume
            navController.navigate(R.id.action_global_inicioFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    private fun actualizarHeaderNavigation() {
        val headerView = binding.navView.getHeaderView(0)
        val nombreUsuarioTextView: TextView = headerView.findViewById(R.id.nombreUsuario)
        val correoUsuarioTextView: TextView = headerView.findViewById(R.id.emailUsuario)

        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("nombreUsuario", "")
        val correoUsuario = sharedPref.getString("email", "")

        // Para que se vea tu usuario y correo en el header del menú desplegable
        nombreUsuarioTextView.text = nombreUsuario
        correoUsuarioTextView.text = correoUsuario
        Log.i("ACTUALIZAR_HEADER", "Recuperado: Email=$correoUsuario, Nombre=$nombreUsuario")
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        with(sharedPref.edit()) {
            Log.i("MainActivity", "Cerrando sesión del usuario")
            remove("email")
            remove("nombreUsuario")
            apply()
        }
        // La actualización se hará automáticamente por el listener al eliminar las preferencias
        navController.navigate(R.id.loginFragment)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    // Configuración del TabLayout para mostrar las categorías
    private fun setupTabLayout() {
        if (tabLayout.tabCount == 0) {
            val categorias = listOf("Todas", "Política", "Deportes", "Tecnología", "Salud", "Economía", "Ciencia", "Cultura", "Opinión")
            for (categoria in categorias) {
                tabLayout.addTab(tabLayout.newTab().setText(categoria))
            }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val categoriaSeleccionada = tab.text.toString()
                    val currentFragment = supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
                    if (currentFragment is InicioFragment) {
                        currentFragment.filtrarPorCategoriaDesdeActivity(categoriaSeleccionada)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    // Estas dos funciones sirven para evitar que el botón de retroceso te
    // lleve a la pantalla de inicio sin haber iniciado sesión.
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (navController.currentDestination?.id) {
            R.id.loginFragment -> {
                finish()
                true
            }
            else -> {
                navController.navigateUp(appBarConfiguration)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination?.id

        if (currentDestination == R.id.loginFragment) {
            finish()
        } else {
            super.onBackPressed()
        }
    }
}