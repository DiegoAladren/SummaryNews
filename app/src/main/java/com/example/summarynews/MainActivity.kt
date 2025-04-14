package com.example.summarynews

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

class MainActivity : AppCompatActivity() {

    //Inicialización de la barra superior
    private lateinit var appBarConfiguration: AppBarConfiguration
    //Inicialización del binding para la actividad principal
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Se obtiene el binding inflando (cargando) MainActivity
        binding = ActivityMainBinding.inflate(layoutInflater)
        //Y se establece la vista con la raiz de el binding que se había definido antes
        setContentView(binding.root)

        //Se establece como ActionBar el elemento include con la appBar
        setSupportActionBar(binding.appBarMain.toolbar)

        // Se añaden las categorías
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        //Se añade una acción al pulsar el botón flotante ( que aparezca un snackbar con el texto en concreto )
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Se han cargado noticias nuevas.", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        //Se definen variables para acceder al diseño del cajón lateral (DrawerLayout), la vista del menú (NavigationView)
        // y el controlador de navegación (NavController), que se encarga de manejar el cambio entre fragments.
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController


        // Se pasan los IDs de los destinos del menú como un conjunto,
        // porque cada uno se debe considerar como una "pantalla principal" o destino de nivel superior.
        // De esta forma se le asignan cada uno de los IDs y el drawer layout para la configuración de la appBar.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        //Se configura la barra superior para que funcione con el controlador de navegación.
        //También se conecta el menú lateral (navView) con el NavController.
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Mostrar u ocultar el TabLayout según el destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.nav_home) {
                tabLayout.visibility = View.VISIBLE

                // Añadir pestañas solo si no se han añadido aún
                if (tabLayout.tabCount == 0) {
                    val categorias = listOf("Todas", "Política", "Deportes", "Tecnología", "Salud", "Economía", "Ciencia", "Cultura", "Opinión")
                    for (categoria in categorias) {
                        tabLayout.addTab(tabLayout.newTab().setText(categoria))
                    }

                    // Escuchar cuando el usuario selecciona una categoría
                    tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab) {
                            val categoriaSeleccionada = tab.text.toString()

                            // Buscar el fragment actual y llamar al método de filtrado
                            val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
                            if (currentFragment is InicioFragment) {
                                currentFragment.filtrarPorCategoriaDesdeActivity(categoriaSeleccionada)
                            }
                        }

                        override fun onTabUnselected(tab: TabLayout.Tab) {}
                        override fun onTabReselected(tab: TabLayout.Tab) {}
                    })
                }

            } else {
                tabLayout.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Infla el menú; esto añade ítems a la barra superior si están definidos.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        // Se maneja lo que ocurre al pulsar el botón de "menú" en la barra superior.
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
