package com.example.summarynews.ui.noticias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.summarynews.R
import androidx.core.content.ContextCompat


class AdaptadorNoticias(private val newsList: List<Noticia>) : RecyclerView.Adapter<AdaptadorNoticias.NoticiasViewHolder>() {

    class NoticiasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTitle)
        val resumen: TextView = itemView.findViewById(R.id.tvSummary)
        val imagen: ImageView = itemView.findViewById(R.id.ivNewsImage)
        val btnLike: ImageButton = itemView.findViewById(R.id.likeButton)
        val btnSave: ImageButton = itemView.findViewById(R.id.saveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NoticiasViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticiasViewHolder, position: Int) {
        val noticias = newsList[position]
        holder.titulo.text = noticias.titulo
        holder.resumen.text = noticias.resumen


        // Puedes añadir clic para abrir la noticia en navegador
        // Glide.with(holder.itemView.context).load(news.imageUrl).into(holder.image)
        holder.imagen.setImageResource(noticias.imagenID)

        // Desde Aquí se controla la detección al darle like a una noticia.
        holder.btnLike.setOnClickListener {
            val isLiked = it.tag as? Boolean == true
            it.tag = !isLiked

            val imageButton = it as ImageButton
            val context = holder.itemView.context
            if (!isLiked) {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
        }

        // Desde Aquí se controla la detección al guardar una noticia.
        holder.btnSave.setOnClickListener {
            val isSaved = it.tag as? Boolean == true
            it.tag = !isSaved

            val imageButton = it as ImageButton
            val context = holder.itemView.context
            if (!isSaved) {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.blue))
            } else {
                imageButton.setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
        }
    }

    override fun getItemCount() = newsList.size
}
