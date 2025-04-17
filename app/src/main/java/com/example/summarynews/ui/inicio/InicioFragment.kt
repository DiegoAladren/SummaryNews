package com.example.summarynews.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentInicioBinding
import com.example.summarynews.db.NoticiaEntity
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.NoticiasViewModel
import kotlinx.coroutines.launch

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorNoticias
    private lateinit var noticiasViewModel: NoticiasViewModel

    private var categoriaActual = "Todas"

    // Se crea el fragment de inicio
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se inicializa el view model para acceder a la base de datos
        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        // Cargar noticias iniciales solo si no hay ninguna en la base de datos
        lifecycleScope.launch {
            if (noticiasViewModel.noticiasLength() == 0) {
                val noticiasIniciales = listOf(
                    NoticiaEntity(
                        titulo = "Transmitir electricidad sin cables ya no es ciencia ficción",
                        resumen = "Transmitir electricidad sin cables parecía cosa de ciencia ficción o, como mucho, " +
                                "una locura de Nikola Tesla en pleno 1901, que imaginó un sistema capaz de " +
                                "transmitirla mediante la ionosfera. Pero más de un siglo después",
                        imagenID = R.drawable.noticia1imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Tecnología",
                        liked = false,
                        saved = false
                    ),
                    NoticiaEntity(
                        titulo = "La previsible derrota de Trump y cómo aprovecharla",
                        resumen = "Apple desarrolla en China " +
                                "alrededor del 90% de su producción total, afirma The New York Times. Hacer un " +
                                "iPhone al 100% en Estados Unidos obligaría a venderlo en 3,500 dólares por unidad, " +
                                "tres veces su precio actual, lo cual desplomaría a sus ventas.",
                        imagenID = R.drawable.noticia2imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Deportes",
                        liked = false,
                        saved = false
                    ),
                    NoticiaEntity(
                        titulo = "EE.UU. e Irán mantienen un diálogo en busca de un nuevo acuerdo nuclear",
                        resumen = "Desde dos salas separadas en Mascate, la capital de Omán, el enviado especial " +
                                "de Donald Trump, Steve Witkoff, y el ministro de Exteriores iraní, Abás " +
                                "Araqchí, han intercambiado a través de un mediador omaní sus líneas rojas " +
                                "en la búsqueda de un nuevo acuerdo nuclear.",
                        imagenID = R.drawable.noticia1imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Política",
                        liked = false,
                        saved = false
                    ),
                    NoticiaEntity(
                        titulo = "La herramienta fitness con la que trabajar brazos",
                        resumen = "Una buena esterilla deportiva, unas mancuernas ajustables o hasta una barra de dominadas " +
                                "sin tornillos son grandes aliados para que nuestra casa se convierta en un " +
                                "pequeño gimnasio.",
                        imagenID = R.drawable.noticia2imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Salud",
                        liked = false,
                        saved = false
                    )
                )
                noticiasViewModel.insertarNoticias(noticiasIniciales)
            }
        }

        // Inicializar el adaptador aquí, antes de observar el LiveData
        adaptador = AdaptadorNoticias(
            emptyList(), // Inicializar con una lista vacía
            onLikeClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
            },
            onSaveClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
            }
        )
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = adaptador

        // Observamos cambios en las noticias desde la base de datos
        noticiasViewModel.noticias.observe(viewLifecycleOwner) { lista ->
            filtrarYActualizarLista(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Aquí se acualiza la lista y se maneja el texto cuando no hay noticias, se pasa la
    // lista al adaptador para que la actualice
    private fun filtrarYActualizarLista(lista: List<NoticiaEntity>) {
        val noticiasFiltradas = if (categoriaActual == "Todas") {
            lista
        } else {
            lista.filter { it.categoria == categoriaActual }
        }

        binding.textoSinNoticias.visibility =
            if (noticiasFiltradas.isEmpty()) View.VISIBLE else View.GONE

        adaptador.actualizarLista(noticiasFiltradas)
    }

    // Aquí se filtra la lista por la categoría seleccionada
    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        categoriaActual = categoria
        noticiasViewModel.noticias.value?.let { lista ->
            filtrarYActualizarLista(lista)
        }
    }
}