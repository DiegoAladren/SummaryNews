package com.example.summarynews.ui.inicio

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentInicioBinding
import com.example.summarynews.db.NoticiaEntity
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.NoticiasViewModel

/**
 * Fragmento principal que muestra la lista de noticias.
 * Permite al usuario ver noticias filtradas por categoría y por su propio ID de usuario.
 * Utiliza un RecyclerView para mostrar las noticias y se comunica con [NoticiasViewModel]
 * para obtener y actualizar los datos de las noticias.
 * También maneja la interacción del usuario como dar "like" o "guardar" una noticia.
 */
class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var adaptador: AdaptadorNoticias
    private lateinit var noticiasViewModel: NoticiasViewModel

    private var categoriaActual = "Todas"
    private var usuarioIdActual: Int = -1

    /**
     * Se llama para que el fragmento instancie su jerarquía de vistas de interfaz de usuario.
     * Infla el layout [FragmentInicioBinding], obtiene el ID del usuario actual desde
     * SharedPreferences ("SesionUsuario") y lo almacena en [usuarioIdActual].
     *
     * @param inflater El LayoutInflater objeto que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la UI del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado
     * guardado anteriormente.
     * @return Devuelve la Vista para la UI del fragmento.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", AppCompatActivity.MODE_PRIVATE)
        usuarioIdActual = sharedPref.getInt("userId", -1)
        Log.i("InicioFragment", "ID de usuario actual: $usuarioIdActual")
        return binding.root
    }

    /**
     * Se llama inmediatamente después de que [onCreateView] haya devuelto la vista, pero antes
     * de que cualquier estado guardado haya sido restaurado en la vista.
     * Inicializa [noticiasViewModel], configura el [adaptador] y el RecyclerView.
     * Observa los cambios en [NoticiasViewModel.todasLasNoticiasLocal] para actualizar la lista
     * de noticias mostrada, filtrándola primero por [usuarioIdActual] y luego por [categoriaActual].
     *
     * @param view La Vista devuelta por [onCreateView].
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo
     * a partir de un estado guardado anteriormente.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

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

        // Observa todas las noticias
        noticiasViewModel.todasLasNoticiasLocal.observe(viewLifecycleOwner) { lista ->
            val noticiasFiltradasPorUsuario = lista.filter { it.usuarioId == usuarioIdActual }
            filtrarYActualizarLista(noticiasFiltradasPorUsuario)
        }
    }

    /**
     * Se llama cuando la vista asociada con el fragmento está siendo destruida.
     * Limpia la referencia a [_binding] para evitar fugas de memoria.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Filtra una lista de noticias según la [categoriaActual]
     * y actualiza el adaptador con el resultado.
     * También gestiona la visibilidad de un TextView que informa al usuario si no hay noticias
     * para mostrar en la categoría actual o en general.
     *
     * @param lista La lista de noticias ya filtrada por el [usuarioIdActual].
     */
    private fun filtrarYActualizarLista(lista: List<NoticiaEntity>) {
        val noticiasFiltradasPorCategoria = if (categoriaActual == "Todas") {
            binding.textoSinNoticias.text = getString(R.string.no_hay_noticias)
            lista
        } else {
            binding.textoSinNoticias.text = getString(R.string.no_hay_noticias_categoria)
            lista.filter { it.categoria == categoriaActual }
        }

        binding.textoSinNoticias.visibility =
            if (noticiasFiltradasPorCategoria.isEmpty()) View.VISIBLE else View.GONE

        adaptador.actualizarLista(noticiasFiltradasPorCategoria)
    }

    /**
     * Permite que un componente externo (MainActivity) solicite el filtrado de noticias
     * por una categoría específica.
     * Actualiza [categoriaActual] y, si el [noticiasViewModel] está inicializado y tiene datos,
     * aplica el proceso de filtrado completo (por usuario y luego por la nueva categoría).
     *
     * @param categoria La nueva categoría por la cual filtrar las noticias.
     */
    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        categoriaActual = categoria
        if (::noticiasViewModel.isInitialized && noticiasViewModel.todasLasNoticiasLocal.value != null) {
            filtrarYActualizarLista(noticiasViewModel.todasLasNoticiasLocal.value!!.filter { it.usuarioId == usuarioIdActual })
        }
    }
}