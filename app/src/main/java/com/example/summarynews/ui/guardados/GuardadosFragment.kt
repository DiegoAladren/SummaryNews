package com.example.summarynews.ui.guardados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.summarynews.databinding.FragmentGuardadosBinding
import com.example.summarynews.ui.noticias.AdaptadorNoticias
import com.example.summarynews.ui.noticias.NoticiasViewModel

class GuardadosFragment : Fragment() {

    private lateinit var binding: FragmentGuardadosBinding
    private lateinit var noticiasViewModel: NoticiasViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuardadosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noticiasViewModel = ViewModelProvider(requireActivity())[NoticiasViewModel::class.java]

        noticiasViewModel.noticias.observe(viewLifecycleOwner) { listaNoticias ->
            val noticiasGuardadas = listaNoticias.filter { it.saved }

            binding.textoSinNoticiasG.visibility =
                if (noticiasGuardadas.isEmpty()) View.VISIBLE else View.GONE

            val adaptador = AdaptadorNoticias(noticiasGuardadas) { noticiaActualizada ->
                noticiasViewModel.actualizarNoticia(noticiaActualizada)
            }

            binding.guardadosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.guardadosRecyclerView.adapter = adaptador
        }
    }
}
