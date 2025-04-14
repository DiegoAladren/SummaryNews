package com.example.summarynews.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentInicioBinding
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.Noticia
import com.example.summarynews.ui.noticias.NoticiasViewModel


class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorNoticias
    private lateinit var todasLasNoticias: List<Noticia>

    private lateinit var noticiasViewModel: NoticiasViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Gestiona todas las noticias
        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        // Aquí es donde se introducen las noticias en el sistema
        if(noticiasViewModel.noticiasLength()==0) {
            todasLasNoticias = listOf(
                Noticia(
                    "La previsible derrota de Trump y cómo aprovecharla",
                    "Apple desarrolla en China " +
                            "alrededor del 90% de su producción total, afirma The New York Times. Hacer un " +
                            "iPhone al 100% en Estados Unidos obligaría a venderlo en 3,500 dólares por unidad, " +
                            "tres veces su precio actual, lo cual desplomaría a sus ventas.",
                    R.drawable.noticia2imagen,
                    "https://example.com",
                    "Política",
                    false,
                    false
                ),
                Noticia(
                    "Transmitir electricidad sin cables ya no es ciencia ficción",
                    "Transmitir electricidad sin cables parecía cosa de ciencia ficción o, como mucho, " +
                            "una locura de Nikola Tesla en pleno 1901, que imaginó un sistema capaz de " +
                            "transmitirla mediante la ionosfera. Pero más de un siglo después",
                    R.drawable.noticia1imagen,
                    "https://example.com",
                    "Tecnología",
                    false,
                    false
                ),
                Noticia(
                    "La herramienta fitness con la que trabajar brazos",
                    "Una buena esterilla deportiva, unas mancuernas ajustables o hasta una barra de dominadas " +
                            "sin tornillos son grandes aliados para que nuestra casa se convierta en un " +
                            "pequeño gimnasio.",
                    R.drawable.noticia2imagen,
                    "https://example.com",
                    "Salud",
                    false,
                    false
                ),
                Noticia(
                    "EE.UU. e Irán mantienen un diálogo en busca de un nuevo acuerdo nuclear",
                    "Desde dos salas separadas en Mascate, la capital de Omán, el enviado especial " +
                            "de Donald Trump, Steve Witkoff, y el ministro de Exteriores iraní, Abás " +
                            "Araqchí, han intercambiado a través de un mediador omaní sus líneas rojas " +
                            "en la búsqueda de un nuevo acuerdo nuclear.",
                    R.drawable.noticia1imagen,
                    "https://example.com",
                    "Política",
                    false,
                    false
                )
            )
        }else{
            todasLasNoticias = noticiasViewModel.getNoticias()!!
        }

        // Guardamos las noticias en el ViewModel
        noticiasViewModel.setNoticias(todasLasNoticias)

        adaptador = AdaptadorNoticias(todasLasNoticias) { noticiaActualizada ->
            noticiasViewModel.actualizarNoticia(noticiaActualizada)
        }

        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = adaptador
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        val noticias = noticiasViewModel.noticias.value ?: return

        val noticiasFiltradas = if (categoria == "Todas") {
            noticias
        } else {
            noticias.filter { it.categoria == categoria }
        }

        // Mostrar el mensaje si no hay noticias
        binding.textoSinNoticias.visibility =
            if (noticiasFiltradas.isEmpty()) View.VISIBLE else View.GONE

        adaptador = AdaptadorNoticias(noticiasFiltradas) { noticiaActualizada ->
            noticiasViewModel.actualizarNoticia(noticiaActualizada)
        }
        binding.newsRecyclerView.adapter = adaptador
    }
}
