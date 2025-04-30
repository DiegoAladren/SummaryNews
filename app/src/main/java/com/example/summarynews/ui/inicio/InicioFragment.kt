package com.example.summarynews.ui.inicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
    private var usuarioIdActual: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        // Recuperar el ID del usuario al crear la vista
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", AppCompatActivity.MODE_PRIVATE)
        usuarioIdActual = sharedPref.getInt("userId", -1) // -1 como valor por defecto si no se encuentra
        Log.i("InicioFragment", "ID de usuario actual: $usuarioIdActual")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("InicioFragment", "No paso yo antes")
        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        lifecycleScope.launch {
            if (noticiasViewModel.noticiasLength(usuarioIdActual) == 0) {
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
                        saved = false,
                        usuarioId = usuarioIdActual
                    ),
                    NoticiaEntity(
                        titulo = "La previsible derrota de Trump y cómo aprovecharla",
                        resumen = "Apple desarrolla en China alrededor del 90% de su producción total, afirma The New York Times. " +
                                "Hacer un iPhone al 100% en Estados Unidos obligaría a venderlo en 3,500 dólares por unidad, " +
                                "tres veces su precio actual, lo cual desplomaría a sus ventas.",
                        imagenID = R.drawable.noticia2imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Deportes",
                        liked = false,
                        saved = false,
                        usuarioId = usuarioIdActual
                    ),
                    NoticiaEntity(
                        titulo = "EE.UU. e Irán mantienen un diálogo en busca de un nuevo acuerdo nuclear",
                        resumen = "Desde dos salas separadas en Mascate, la capital de Omán, el enviado especial " +
                                "de Donald Trump, Steve Witkoff, y el ministro de Exteriores iraní, Abás Araqchí, " +
                                "han intercambiado a través de un mediador omaní sus líneas rojas " +
                                "en la búsqueda de un nuevo acuerdo nuclear.",
                        imagenID = R.drawable.noticia1imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Política",
                        liked = false,
                        saved = false,
                        usuarioId = usuarioIdActual
                    ),
                    NoticiaEntity(
                        titulo = "La herramienta fitness con la que trabajar brazos",
                        resumen = "Una buena esterilla deportiva, unas mancuernas ajustables o hasta una barra de dominadas " +
                                "sin tornillos son grandes aliados para que nuestra casa se convierta en un pequeño gimnasio.",
                        imagenID = R.drawable.noticia2imagen,
                        fuenteURL = "https://example.com",
                        categoria = "Salud",
                        liked = false,
                        saved = false,
                        usuarioId = usuarioIdActual
                    )
                )
                noticiasViewModel.insertarNoticias(noticiasIniciales)
            }
        }

        adaptador = AdaptadorNoticias(
            emptyList(),
            onLikeClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
            },
            onSaveClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
            }
        )

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = adaptador

        noticiasViewModel.noticias.observe(viewLifecycleOwner) { lista ->
            filtrarYActualizarLista(lista)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun filtrarYActualizarLista(lista: List<NoticiaEntity>) {
        val noticiasFiltradasPorCategoria = if (categoriaActual == "Todas") {
            lista
        } else {
            lista.filter { it.categoria == categoriaActual }
        }

        // Ahora filtra también por el ID de usuario
        val noticiasFiltradasFinal = noticiasFiltradasPorCategoria.filter { it.usuarioId == usuarioIdActual }

        binding.textoSinNoticias.visibility =
            if (noticiasFiltradasFinal.isEmpty()) View.VISIBLE else View.GONE

        adaptador.actualizarLista(noticiasFiltradasFinal)
    }

    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        categoriaActual = categoria
        if (::noticiasViewModel.isInitialized && noticiasViewModel.noticias.value != null) {
            filtrarYActualizarLista(noticiasViewModel.noticias.value!!)
        }
    }
}
