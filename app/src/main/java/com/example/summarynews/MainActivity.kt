package com.example.summarynews

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
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
import androidx.lifecycle.Observer
import com.example.summarynews.ui.registro.LoginViewModel
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.summarynews.ui.noticias.NoticiasViewModel
import com.example.summarynews.ui.noticias.NoticiasViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: androidx.navigation.NavController
    private lateinit var tabLayout: TabLayout
    private val loginViewModel: LoginViewModel by viewModels()
    private var currentUserId: Int? = null
    private lateinit var noticiasViewModel: NoticiasViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
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

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Se han cargado noticias nuevas.", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        verificarSesionInicial()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_liked, R.id.nav_saved
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                }
                R.id.nav_liked -> {
                    navController.navigate(R.id.nav_liked)
                }
                R.id.nav_saved -> {
                    navController.navigate(R.id.nav_saved)
                }
                R.id.nav_logout -> {
                    cerrarSesion()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START) // Cerrar el drawer después de seleccionar un ítem
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_home) {
                // Obtener el userId solo cuando estamos en el fragmento principal
                currentUserId?.let { userId ->
                    noticiasViewModel =
                        ViewModelProvider(this, NoticiasViewModelFactory(application, userId))[NoticiasViewModel::class.java]
                    setupTabLayout()
                }
                tabLayout.visibility = View.VISIBLE
                binding.appBarMain.fab.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                setupTabLayout()
            } else {
                tabLayout.visibility = View.GONE
                binding.appBarMain.fab.visibility = View.GONE
                if (destination.id == R.id.loginFragment || destination.id == R.id.registroFragment) {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                } else {
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
        }

        loginViewModel.navigateToInicio.observe(this, Observer { shouldNavigate ->
            if (shouldNavigate) {
                obtenerIdUsuarioActual() // Obtener el ID después del login/registro
                navController.navigate(R.id.action_global_inicioFragment)
                loginViewModel.resetNavigateToInicio()
            }
        })
    }

    private fun obtenerIdUsuarioActual() {
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        currentUserId = sharedPref.getInt("userId", -1).takeIf { it != -1 }
    }

    private fun verificarSesionInicial() {
        val sharedPref = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        if (userId == -1) {
            navController.navigate(R.id.loginFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            currentUserId = userId
            navController.navigate(R.id.action_global_inicioFragment)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    private fun cerrarSesion() {
        // Eliminar la información de la sesión de SharedPreferences
        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            remove("email")
            apply()
        }

        // Navegar de vuelta al LoginFragment
        navController.navigate(R.id.loginFragment)

        // Bloquear el DrawerLayout ya que no hay sesión activa
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        currentUserId = null // Resetear el userId al cerrar sesión
    }

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

// Factory para el NoticiasViewModel que requiere un parámetro
class NoticiasViewModelFactory(
    private val application: Application,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoticiasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoticiasViewModel(application, userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}