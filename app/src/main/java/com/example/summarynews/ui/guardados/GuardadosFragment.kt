package com.example.summarynews.ui.guardados

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.databinding.FragmentGuardadosBinding
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.NoticiasViewModel

/**
 * Un [Fragment] que muestra una lista de artículos de noticias guardados por el usuario actual.
 *
 * Este fragmento recupera datos de noticias de un [NoticiasViewModel], los filtra para mostrar solo
 * los artículos marcados como 'guardados' y asociados con el usuario que ha iniciado sesión,
 * y los muestra en un RecyclerView usando [AdaptadorNoticias].
 */
class GuardadosFragment : Fragment() {

    private lateinit var binding: FragmentGuardadosBinding
    private lateinit var noticiasViewModel: NoticiasViewModel
    private lateinit var adaptador: AdaptadorNoticias

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
        binding = FragmentGuardadosBinding.inflate(inflater, container, false)
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", AppCompatActivity.MODE_PRIVATE)
        usuarioIdActual = sharedPref.getInt("userId", -1) // -1 como valor por defecto si no se encuentra
        Log.i("InicioFragment", "ID de usuario actual: $usuarioIdActual")
        return binding.root
    }

    /**
     * Se llama inmediatamente después de que [onCreateView] haya devuelto un valor, pero antes de que
     * se haya restaurado cualquier estado guardado en la vista.
     *
     * Este método inicializa el [NoticiasViewModel] y el [AdaptadorNoticias], configura el
     * RecyclerView e inicia la carga de noticias guardadas.
     *
     * @param view La Vista devuelta por [onCreateView].
     * @param savedInstanceState Si no es nulo, este fragmento está siendo reconstruido
     * a partir de un estado guardado previamente.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        // Inicializar el adaptador con una lista vacía
        adaptador = AdaptadorNoticias(
            emptyList(),
            onLikeClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
            },
            onSaveClicked = { noticia ->
                noticiasViewModel.actualizarNoticia(noticia)
                // Solo recargar la lista cuando se guarda o se quita de guardados para que
                // se elimine la noticia en tiempo real.
                cargarNoticiasGuardadas()
            }
        )
        binding.guardadosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.guardadosRecyclerView.adapter = adaptador

        // Cargar las noticias guardadas inicialmente
        cargarNoticiasGuardadas()
    }

    /**
     * Carga los artículos de noticias guardados del [NoticiasViewModel] y actualiza la UI.
     *
     * Este método observa los cambios en el LiveData `noticias` del ViewModel,
     * filtra la lista para incluir solo los artículos marcados como 'guardados' y asociados
     * con el [usuarioIdActual], y luego actualiza el [AdaptadorNoticias].
     * También gestiona la visibilidad de un mensaje de "no hay noticias" si no hay
     * noticias guardadas por el usuario.
     */
    private fun cargarNoticiasGuardadas() {
        noticiasViewModel.noticias.observe(viewLifecycleOwner) { listaNoticias ->
            val noticiasGuardadas = listaNoticias.filter { it.saved && it.usuarioId == usuarioIdActual }

            binding.textoSinNoticiasG.visibility =
                if (noticiasGuardadas.isEmpty()) View.VISIBLE else View.GONE

            adaptador.actualizarLista(noticiasGuardadas)
        }
    }
}