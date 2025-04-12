package com.example.summarynews.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.R
import com.example.summarynews.databinding.FragmentHomeBinding
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.Noticia

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: AdaptadorNoticias

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noticiasEjemplo = listOf(
            Noticia("La previsible derrota de Trump y cómo aprovecharla", "Apple desarrolla en China alrededor del 90% de su producción total, afirma The New York Times. Hacer un iPhone al 100% en Estados Unidos obligaría a venderlo en 3,500 dólares por unidad, tres veces su precio actual, lo cual desplomaría a sus ventas.", R.drawable.noticia2imagen,  "https://example.com"),
            Noticia("Titular 2", "Resumen de la noticia 2", R.drawable.noticia1imagen, "https://example.com"),
            Noticia("Titular 2", "Resumen de la noticia 2", R.drawable.noticia2imagen, "https://example.com"),
            Noticia("Titular 2", "Resumen de la noticia 2", R.drawable.noticia1imagen, "https://example.com")
        )

        newsAdapter = AdaptadorNoticias(noticiasEjemplo)
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = newsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
