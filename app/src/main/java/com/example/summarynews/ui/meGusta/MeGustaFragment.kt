package com.example.summarynews.ui.meGusta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.databinding.FragmentMegustaBinding
import com.example.summarynews.ui.noticias.NoticiasViewModel

class MeGustaFragment : Fragment() {

    private lateinit var binding: FragmentMegustaBinding
    private lateinit var noticiasViewModel: NoticiasViewModel
    private lateinit var adaptador: LikesPorCategoriaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMegustaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        adaptador = LikesPorCategoriaAdapter(emptyList())
        binding.recyclerLikesPorCategoria.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLikesPorCategoria.adapter = adaptador

        observarEstadisticas()
    }

    private fun observarEstadisticas() {
        noticiasViewModel.noticias.observe(viewLifecycleOwner) { listaNoticias ->
            val noticiasLikes = listaNoticias.filter { it.liked }

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
                "Categoría menos te gusta: Ninguna"
            }

            // Actualizar el RecyclerView de likes por categoría
            adaptador.actualizarLista(likesPorCategoria)
        }
    }
}