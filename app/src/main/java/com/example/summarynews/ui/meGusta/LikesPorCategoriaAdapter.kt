package com.example.summarynews.ui.meGusta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.summarynews.R

class LikesPorCategoriaAdapter(private var likesPorCategoria: List<Pair<String, Int>>) :
    RecyclerView.Adapter<LikesPorCategoriaAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoriaTextView: TextView = itemView.findViewById(R.id.text_categoria)
        val likesTextView: TextView = itemView.findViewById(R.id.text_likes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_likes_por_categoria, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (categoria, likes) = likesPorCategoria[position]
        holder.categoriaTextView.text = categoria
        holder.likesTextView.text = "$likes likes"
    }

    override fun getItemCount(): Int = likesPorCategoria.size

    fun actualizarLista(nuevaLista: List<Pair<String, Int>>) {
        likesPorCategoria = nuevaLista
        notifyDataSetChanged()
    }
}