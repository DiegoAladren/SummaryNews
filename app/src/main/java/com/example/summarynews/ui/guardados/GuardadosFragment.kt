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

class GuardadosFragment : Fragment() {

    private lateinit var binding: FragmentGuardadosBinding
    private lateinit var noticiasViewModel: NoticiasViewModel
    private lateinit var adaptador: AdaptadorNoticias

    private var usuarioIdActual: Int = -1

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
                // se elimine la noticia si le quitas de guardados
                cargarNoticiasGuardadas()
            }
        )
        binding.guardadosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.guardadosRecyclerView.adapter = adaptador

        // Cargar las noticias guardadas inicialmente
        cargarNoticiasGuardadas()
    }

    private fun cargarNoticiasGuardadas() {
        noticiasViewModel.noticias.observe(viewLifecycleOwner) { listaNoticias ->
            val noticiasGuardadas = listaNoticias.filter { it.saved && it.usuarioId == usuarioIdActual }

            binding.textoSinNoticiasG.visibility =
                if (noticiasGuardadas.isEmpty()) View.VISIBLE else View.GONE

            adaptador.actualizarLista(noticiasGuardadas)
        }
    }
}