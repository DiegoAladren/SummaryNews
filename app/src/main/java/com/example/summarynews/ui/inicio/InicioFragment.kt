package com.example.summarynews.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentInicioBinding
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.Noticia

class InicioFragment : Fragment() {

    //Se crea un binding ( que sería como un vinculador en español ) para la vinculación de vistas.
    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    // Se inicializa un adaptador de noticias.
    private lateinit var adaptador: AdaptadorNoticias
    private lateinit var todasLasNoticias: List<Noticia>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todasLasNoticias = listOf(
            Noticia("La previsible derrota de Trump y cómo aprovecharla", "Apple desarrolla en China alrededor del 90% de su producción total, afirma The New York Times. Hacer un iPhone al 100% en Estados Unidos obligaría a venderlo en 3,500 dólares por unidad, tres veces su precio actual, lo cual desplomaría a sus ventas.", R.drawable.noticia2imagen,  "https://example.com", "Política"),
            Noticia("Transmitir electricidad sin cables ya no es ciencia ficción", "Transmitir electricidad sin cables parecía cosa de ciencia ficción o, como mucho, una locura de Nikola Tesla en pleno 1901, que imaginó un sistema capaz de transmitirla mediante la ionosfera. Pero más de un siglo después", R.drawable.noticia1imagen, "https://example.com", "Tecnología"),
            Noticia("La herramienta fitness con la que trabajar brazos", "Una buena esterilla deportiva, unas mancuernas ajustables o hasta una barra de dominadas sin tornillos son grandes aliados para que nuestra casa se convierta en un pequeño gimnasio.", R.drawable.noticia2imagen, "https://example.com", "Salud"),
            Noticia("EE.UU. e Irán mantienen un diálogo en busca de un nuevo acuerdo nuclear", "Desde dos salas separadas en Mascate, la capital de Omán, el enviado especial de Donald Trump, Steve Witkoff, y el ministro de Exteriores iraní, Abás Araqchí, han intercambiado a través de un mediador omaní sus líneas rojas en la búsqueda de un nuevo acuerdo nuclear.", R.drawable.noticia1imagen, "https://example.com", "Política")
        )

        adaptador = AdaptadorNoticias(todasLasNoticias)
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = adaptador
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Este método se llama desde MainActivity
    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        val noticiasFiltradas = if (categoria == "Todas") {
            todasLasNoticias
        } else {
            todasLasNoticias.filter { it.categoria == categoria }
        }

        // Mostrar el mensaje si no hay noticias
        if (noticiasFiltradas.isEmpty()) {
            binding.textoSinNoticias.visibility = View.VISIBLE
        } else {
            binding.textoSinNoticias.visibility = View.GONE
        }

        adaptador = AdaptadorNoticias(noticiasFiltradas)
        binding.newsRecyclerView.adapter = adaptador
    }

}
