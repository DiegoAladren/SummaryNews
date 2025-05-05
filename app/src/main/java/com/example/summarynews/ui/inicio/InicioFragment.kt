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
import com.example.summarynews.databinding.FragmentInicioBinding
import com.example.summarynews.db.NoticiaEntity
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.NoticiasViewModel

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
        val sharedPref = requireActivity().getSharedPreferences("SesionUsuario", AppCompatActivity.MODE_PRIVATE)
        usuarioIdActual = sharedPref.getInt("userId", -1)
        Log.i("InicioFragment", "ID de usuario actual: $usuarioIdActual")
        return binding.root
    }

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

        binding.textoSinNoticias.visibility =
            if (noticiasFiltradasPorCategoria.isEmpty()) View.VISIBLE else View.GONE

        adaptador.actualizarLista(noticiasFiltradasPorCategoria)
    }

    fun filtrarPorCategoriaDesdeActivity(categoria: String) {
        categoriaActual = categoria
        if (::noticiasViewModel.isInitialized && noticiasViewModel.todasLasNoticiasLocal.value != null) {
            filtrarYActualizarLista(noticiasViewModel.todasLasNoticiasLocal.value!!.filter { it.usuarioId == usuarioIdActual })
        }
    }
}