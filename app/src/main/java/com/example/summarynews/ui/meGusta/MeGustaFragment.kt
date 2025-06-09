package com.example.summarynews.ui.meGusta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.databinding.FragmentMegustaBinding
import com.example.summarynews.ui.noticias.NoticiasViewModel

/**
 * Un [Fragment] que muestra estadísticas sobre las noticias a las que el usuario actual ha dado "me gusta".
 *
 * Este fragmento obtiene los datos de las noticias de un [NoticiasViewModel], filtra las noticias
 * que han recibido un "me gusta" por parte del usuario con la sesión activa y calcula estadísticas como
 * el total de "me gusta" y la distribución de "me gusta" por categoría.
 * Las estadísticas por categoría se muestran en un RecyclerView usando [LikesPorCategoriaAdapter].
 */
class MeGustaFragment : Fragment() {

    private lateinit var binding: FragmentMegustaBinding
    private lateinit var noticiasViewModel: NoticiasViewModel
    private lateinit var adaptador: LikesPorCategoriaAdapter

    private var usuarioIdActual: Int = -1

    /**
     * Infla el layout usando view binding y recupera el ID del usuario actual
     * de las preferencias compartidas.
     *
     * @param inflater El LayoutInflater objeto que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la UI del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado
     * guardado anteriormente.
     * @return Devuelve la Vista para la UI del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMegustaBinding.inflate(inflater, container, false)
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", AppCompatActivity.MODE_PRIVATE)
        usuarioIdActual = sharedPref.getInt("userId", -1) // -1 como valor por defecto si no se encuentra
        Log.i("InicioFragment", "ID de usuario actual: $usuarioIdActual")
        return binding.root
    }

    /**
     * Se llama inmediatamente después de que [onCreateView] haya devuelto un valor, pero antes de que
     * se haya restaurado cualquier estado guardado en la vista.
     *
     * Este método inicializa el [NoticiasViewModel] y el [LikesPorCategoriaAdapter],
     * configura el RecyclerView y comienza a observar las estadísticas de "me gusta".
     *
     * @param view La [View] devuelta por [onCreateView].
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido
     * a partir de un estado guardado previamente.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        adaptador = LikesPorCategoriaAdapter(emptyList())
        binding.recyclerLikesPorCategoria.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLikesPorCategoria.adapter = adaptador

        observarEstadisticas()
    }

    /**
     * Observa los cambios en la lista de noticias del [NoticiasViewModel] y calcula
     * y muestra las estadísticas de "me gusta" del usuario.
     *
     * Este método filtra las noticias para incluir solo las que el [usuarioIdActual] ha marcado
     * como "me gusta", calcula el total de "me gusta", la distribución por categoría,
     * y las categorías con más y menos "me gusta". Finalmente, actualiza la UI y el
     * [LikesPorCategoriaAdapter] con estos datos.
     */
    private fun observarEstadisticas() {
        noticiasViewModel.noticias.observe(viewLifecycleOwner) { listaNoticias ->
            val noticiasLikes = listaNoticias.filter { it.liked && it.usuarioId == usuarioIdActual }

            // Calcular el total de likes
            val totalLikes = noticiasLikes.size
            binding.totalLikes.text = "Total de likes dados: $totalLikes"

            // Calcular likes por categoría
            val likesPorCategoria = noticiasLikes.groupingBy { it.categoria }
                .eachCount()
                .toList()
                .sortedByDescending { (_, count) -> count }

            // Encontrar la categoría con más likes
            val categoriaMasLikes = likesPorCategoria.firstOrNull()
            binding.categoriaMasLikes.text = if (categoriaMasLikes != null) {
                "Categoría que más te gusta: ${categoriaMasLikes.first} (${categoriaMasLikes.second} likes)"
            } else {
                "Categoría que más te gusta: Ninguna"
            }

            // Encontrar la categoría con menos likes (si hay alguna)
            val categoriaMenosLikes = likesPorCategoria.lastOrNull()
            binding.categoriaMenosLikes.text = if (categoriaMenosLikes != null) {
                "Categoría que menos te gusta: ${categoriaMenosLikes.first} (${categoriaMenosLikes.second} likes)"
            } else {
                "Categoría que menos te gusta: Ninguna"
            }

            // Actualizar el RecyclerView de likes por categoría
            adaptador.actualizarLista(likesPorCategoria)
        }
    }
}