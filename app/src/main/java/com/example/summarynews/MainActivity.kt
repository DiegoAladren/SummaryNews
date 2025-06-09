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
import androidx.navigation.fragment.NavHostFragment
import com.example.summarynews.ui.noticias.NoticiasViewModel

/**
 * Actividad principal de la aplicación. Gestiona la navegación, la barra de herramientas,
 * el panel lateral de navegación (drawer), el tema de la aplicación y la sesión del usuario.
 */
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

    /**
     * Se llama cuando la actividad se está iniciando.
     * Es aquí donde se debe realizar la mayor parte de la inicialización:
     * - Aplicar el tema guardado.
     * - Inflar la jerarquía de vistas de la actividad.
     * - Inicializar la interfaz de usuario (Toolbar, NavController, DrawerLayout, NavView).
     * - Configurar el Floating Action Button (FAB), que sirve para cargar noticias.
     * - Comprobar la sesión del usuario si es la primera creación de la actividad.
     * - Inicializar la configuración de la AppBar.
     * - Configurar la navegación de la aplicación (listeners, observadores).
     *
     * @param savedInstanceState Si la actividad se está reinicializando después de haber sido
     * previamente cerrada, este Bundle contiene los datos más
     * recientes en [onSaveInstanceState]. De lo contrario, es nulo.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        aplicarTemaGuardado()

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initInterfaz()
        initFab()

        // Solo comprueba la sesión inicial y navega si la actividad se crea por primera vez
        // (savedInstanceState es null). Si no es null, el estado de navegación se restaurará
        // automáticamente por el NavController.
        if (savedInstanceState == null) {
            verificarSesionInicial()
        }

        initAppBar()
        configuracionNavegacionApp()
    }

    /**
     * Aplica el tema (claro/oscuro) guardado en [SharedPreferences].
     * Este método se llama antes de `super.onCreate()` y `setContentView()` para asegurar
     * que el tema se aplique correctamente desde el principio.
     */
    private fun aplicarTemaGuardado() {
        // Aplicar el tema guardado ANTES de super.onCreate y setContentView
        val appSettingsPrefs = getSharedPreferences(PREF_APP_SETTINGS, MODE_PRIVATE)
        val isDarkModeEnabled =
            appSettingsPrefs.getBoolean(PREF_KEY_DARK_MODE, isSystemInDarkMode())
        applyAppTheme(isDarkModeEnabled)
    }

    /**
     * Inicializa los componentes principales de la interfaz de usuario.
     * Configura la Toolbar, obtiene referencias al [TabLayout], [DrawerLayout], [NavigationView]
     * y obtiene el [navController] del [NavHostFragment].
     */
    private fun initInterfaz() {
        setSupportActionBar(binding.appBarMain.toolbar) // Configura la toolbar principal

        tabLayout = findViewById(R.id.tab_layout) // Referencia al TabLayout (barra de pestañas)
        drawerLayout = binding.drawerLayout // Referencia al DrawerLayout desde el binding (menú lateral)
        navView = binding.navView // Referencia a la NavigationView desde el binding

        // Obtiene el NavController del NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
    }

    /**
     * Inicializa y configura el Floating Action Button (FAB).
     * El FAB permite al usuario cargar nuevas noticias. Muestra un [Snackbar] mientras se cargan
     * las noticias y otro al finalizar la carga. Evita múltiples clics mientras está cargando.
     */
    private fun initFab() {
        var estaCargando = false // Flag para controlar el estado de carga

        // Configura el botón flotante que carga noticias al pulsarlo.
        binding.appBarMain.fab.setOnClickListener { view ->
            val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
            val idUsuario = sharedPref.getInt(PREF_KEY_USER_ID, -1)

            if (estaCargando) return@setOnClickListener // Salir si ya está en carga

            estaCargando = true
            Log.i(TAG, "Cargando nuevas noticias desde el FAB" + estaCargando)
            binding.appBarMain.fab.isEnabled = false
            val snackbarCargando =
                Snackbar.make(view, "Cargando nuevas noticias...", Snackbar.LENGTH_INDEFINITE)
                    .setAnchorView(R.id.fab)
            snackbarCargando.show()

            noticiasViewModel.cargarNuevasNoticias("us", idUsuario) {
                estaCargando = false
                Log.i(TAG, "Noticias cargadas correctamente" + estaCargando)
                binding.appBarMain.fab.isEnabled = true
                snackbarCargando.dismiss()

                Snackbar.make(view, "Noticias cargadas correctamente", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.fab)
                    .show()
            }
        }
    }

    /**
     * Inicializa la configuración de la AppBar (Barra de Aplicación).
     * Crea una [AppBarConfiguration] con los destinos de nivel superior y el [DrawerLayout].
     * Luego, configura la ActionBar y la [NavigationView] para que funcionen con el [navController].
     */
    private fun initAppBar() {
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_liked, R.id.nav_saved
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    /**
     * Configura los diferentes aspectos de la navegación de la aplicación.
     * Esto incluye la navegación del Drawer, los listeners para cambios de destino,
     * la observación del [LoginViewModel] y la configuración del listener de [SharedPreferences].
     */
    private fun configuracionNavegacionApp() {
        configurarDrawerNavigation()

        configurarListenerDestinos()

        observarLoginViewModel()
        // Inicializamos el listener para SharedPreferences
        configurarPreferenciasListener()
    }

    /**
     * Configura el listener para los ítems del menú del [NavigationView] (Drawer o menú lateral).
     * Define la acción a realizar cuando se selecciona cada ítem del menú,
     * como navegar a diferentes fragmentos o cerrar la sesión del usuario.
     * Cierra el drawer después de seleccionar un ítem.
     */
    private fun configurarDrawerNavigation() {
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
    }

    /**
     * Configura un listener para los cambios de destino en el [navController].
     * Este listener ajusta la UI (visibilidad del [TabLayout] y FAB, bloqueo del drawer)
     * según el fragmento actual.
     * Si el destino es [InicioFragment] (`nav_home`):
     * - Muestra el [TabLayout] y el FAB.
     * - Desbloquea el [DrawerLayout].
     * - Configura el [TabLayout] con las categorías.
     * - Selecciona la pestaña "Todas" por defecto.
     * - Fuerza el filtrado inicial por la categoría "Todas".
     * Si el destino no es [InicioFragment]:
     * - Oculta el [TabLayout] y el FAB.
     * - Bloquea el [DrawerLayout] si el destino es `loginFragment`, `registroFragment` o `nav_ajustes`.
     * - Desbloquea el [DrawerLayout] para otros destinos (`nav_liked`, `nav_saved`).
     * Finalmente, invalida el menú de opciones para que [onPrepareOptionsMenu] sea llamado.
     */
    private fun configurarListenerDestinos() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_home) {
                tabLayout.visibility = View.VISIBLE
                binding.appBarMain.fab.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                setupTabLayout()

                // Seleccionar por defecto el tab "Todas"
                val tabTodas = (0 until tabLayout.tabCount)
                    .map { tabLayout.getTabAt(it) }
                    .firstOrNull { it?.text == DEFAULT_TAB_TEXT }
                tabTodas?.select()

                // Forzar filtrado por categoría "Todas" cuando entras a InicioFragment
                val currentFragment = supportFragmentManager
                    .primaryNavigationFragment?.childFragmentManager?.primaryNavigationFragment
                if (currentFragment is InicioFragment) {
                    currentFragment.filtrarPorCategoriaDesdeActivity(DEFAULT_TAB_TEXT)
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
    }

    /**
     * Observa el [LoginViewModel] para detectar cuándo se debe navegar al [InicioFragment].
     * Esto ocurre, por ejemplo, después de un inicio de sesión o registro exitoso.
     * Una vez que se realiza la navegación, se reinicia el estado en el ViewModel.
     */
    private fun observarLoginViewModel() {
        loginViewModel.navigateToInicio.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate) {
                navController.navigate(R.id.action_global_inicioFragment)
                loginViewModel.resetNavigateToInicio()
            }
        })
    }

    /**
     * Configura el [SharedPreferences.OnSharedPreferenceChangeListener].
     * Este listener se activa cuando cambian los valores en [SharedPreferences].
     * Específicamente, si cambian `PREF_KEY_USERNAME` o `PREF_KEY_EMAIL`,
     * se llama a [actualizarHeaderNavigation] para refrescar la información del usuario
     * en el header del [NavigationView].
     */
    private fun configurarPreferenciasListener() {
        preferenceChangeListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == PREF_KEY_USERNAME || key == PREF_KEY_EMAIL) {
                    actualizarHeaderNavigation()
                }
            }
    }

    /**
     * Aplica el tema de la aplicación (claro u oscuro).
     * @param isDarkMode `true` para activar el modo oscuro, `false` para el modo claro.
     */
    private fun applyAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * COmprueba si el sistema operativo está actualmente en modo oscuro.
     * @return `true` si el sistema está en modo oscuro, `false` en caso contrario.
     */
    private fun isSystemInDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Se llama cuando se selecciona un ítem del menú de opciones de la ActionBar.
     * Maneja la navegación al fragmento de ajustes (`AjustesFragment`) cuando se selecciona
     * la opción correspondiente.
     *
     * @param item El ítem del menú que ha sido seleccionado.
     * @return `true` si el evento se maneja, `false` en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.action_global_to_ajustesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Se llama justo antes de que el menú de opciones sea mostrado.
     * Se utiliza para modificar dinámicamente el menú, como ocultar el ítem de "Ajustes"
     * si el usuario ya se encuentra en la pantalla de Ajustes.
     *
     * @param menu El menú de opciones en el que se están colocando los ítems.
     * @return Devuelve `true` para que el menú se muestre; si devuelve `false` no se mostrará.
     */
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val isEnAjustes = navController.currentDestination?.id == R.id.nav_ajustes
        menu.findItem(R.id.action_settings)?.isVisible = !isEnAjustes
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Se llama cuando la actividad va a empezar a interactuar con el usuario.
     * En este punto, la actividad está en la cima de la pila de actividades.
     * Registra el [preferenceChangeListener] para escuchar cambios en [SharedPreferences].
     * Llama a [actualizarHeaderNavigation] para asegurar que la información del usuario
     * en el header del [NavigationView] esté actualizada al mostrar la actividad.
     */
    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
        // Registramos el listener para escuchar los cambios
        sharedPref.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        // Llamamos a actualizar al iniciar la actividad para mostrar los datos iniciales si hay sesión
        actualizarHeaderNavigation()
    }

    /**
     * Se llama cuando el sistema está a punto de detener la actividad.
     * Esto puede suceder porque la actividad está siendo finalizada o porque otra actividad
     * está tomando el foco.
     * Desregistra el [preferenceChangeListener] para evitar fugas de memoria.
     */
    override fun onPause() {
        super.onPause()
        val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
        // Desregistramos el listener para evitar fugas de memoria
        sharedPref.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    /**
     * Comprueba si ya existe una sesión de usuario al iniciar la actividad.
     * Navega al LoginFragment si no hay sesión, o al InicioFragment si la hay.
     */
    private fun verificarSesionInicial() {
        val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
        val emailGuardado = sharedPref.getString(PREF_KEY_EMAIL, null)

        if (emailGuardado == null) {
            navController.navigate(R.id.loginFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            // La actualización inicial se hará en onResume
            navController.navigate(R.id.action_global_inicioFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    /**
     * Actualiza el nombre y correo del usuario en el header del [NavigationView].
     * Obtiene el nombre y correo electrónico del usuario desde [SharedPreferences]
     * y los establece en los [TextView] correspondientes del header.
     */
    private fun actualizarHeaderNavigation() {
        val headerView = binding.navView.getHeaderView(0)
        val nombreUsuarioTextView: TextView = headerView.findViewById(R.id.nombreUsuario)
        val correoUsuarioTextView: TextView = headerView.findViewById(R.id.emailUsuario)

        val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString(PREF_KEY_USERNAME, "")
        val correoUsuario = sharedPref.getString(PREF_KEY_EMAIL, "")

        // Para que se vea tu usuario y correo en el header del menú desplegable
        nombreUsuarioTextView.text = nombreUsuario
        correoUsuarioTextView.text = correoUsuario
        Log.i("ACTUALIZAR_HEADER", "Recuperado: Email=$correoUsuario, Nombre=$nombreUsuario")
    }

    /**
     * Cierra la sesión del usuario actual.
     * Elimina el email y el nombre de usuario de [SharedPreferences] (PREF_USER_SESSION).
     * Navega a la pantalla de LoginFragment.
     * Bloquea el [DrawerLayout] para que no se pueda abrir en la pantalla de login.
     * La actualización del header del drawer ocurrirá automáticamente debido al
     * [preferenceChangeListener] que detecta la eliminación de las claves.
     */
    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences(PREF_USER_SESSION, MODE_PRIVATE)
        with(sharedPref.edit()) {
            Log.i(TAG, "Cerrando sesión del usuario")
            remove(PREF_KEY_EMAIL)
            remove(PREF_KEY_USERNAME)
            apply()
        }
        // La actualización se hará automáticamente por el listener al eliminar las preferencias
        navController.navigate(R.id.loginFragment)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    /**
     * Configura el [TabLayout] con las categorías de noticias si aún no ha sido configurado.
     * Añade pestañas para cada categoría definida en [TAB_CATEGORIES].
     * Establece un [TabLayout.OnTabSelectedListener] para que, cuando se seleccione una pestaña,
     * se notifique al [InicioFragment] actual (si es visible) para que filtre las noticias
     * por la categoría seleccionada.
     */
    private fun setupTabLayout() {
        if (tabLayout.tabCount == 0) {
            val categorias = TAB_CATEGORIES
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

    /**
     * Inicializa el contenido del menú de opciones de la actividad.
     * Infla el menú desde el recurso XML `R.menu.main`.
     *
     * @param menu El menú de opciones en el que se están colocando los ítems.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    /**
     * Maneja la navegación "hacia arriba" (botón de retroceso en la ActionBar).
     * En concreto, cierra la app si se está en LoginFragment y se presiona "atrás" en la toolbar
     * para no poder volver desde el inicio de sesión a una sesión previamente cerrada.
     * @return `true` si la navegación hacia arriba ha sido manejada por esta implementación,
     * `false` en caso contrario.
     */
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

    /**
     * Se llama cuando se presiona el botón de retroceso del dispositivo.
     * Si el destino actual es LoginFragment, finaliza la actividad. Esto evita que el usuario,
     * después de cerrar sesión y volver a la pantalla de login, pueda presionar "atrás" y
     * volver a la pantalla principal.
     * En otros casos, se comporta de la manera predeterminada (delega a la superclase).
     *
     */
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

    /**
     * Objeto que contiene constantes utilizadas en [MainActivity].
     * Estas constantes incluyen etiquetas para logs (TAG), nombres y claves para [SharedPreferences]
     * relacionadas con los ajustes de la aplicación (tema) y la sesión del usuario (ID, email, nombre),
     * y la lista de categorías de noticias para el [TabLayout].
     */
    companion object {
        private const val TAG = "MainActivity" // Para logs

        // SharedPreferences para ajustes de la app (tema)
        private const val PREF_APP_SETTINGS = "AjustesAppSummaryNews"
        private const val PREF_KEY_DARK_MODE = "dark_mode_enabled"

        // SharedPreferences para la sesión del usuario
        private const val PREF_USER_SESSION = "SesionUsuario"
        private const val PREF_KEY_USER_ID = "userId"
        private const val PREF_KEY_EMAIL = "email"
        private const val PREF_KEY_USERNAME = "nombreUsuario"

        private val TAB_CATEGORIES = listOf(
            "Todas", "Política", "Deportes", "Tecnología", "Salud",
            "Economía", "Ciencia", "Cultura", "Opinión"
        )
        private const val DEFAULT_TAB_TEXT = "Todas"
    }
}