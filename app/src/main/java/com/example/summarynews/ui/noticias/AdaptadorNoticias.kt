package com.example.summarynews.ui.noticias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.summarynews.R
import androidx.core.content.ContextCompat


class AdaptadorNoticias(
    private val newsList: List<Noticia>,
    private val onGuardarClicked: (Noticia) -> Unit
) : RecyclerView.Adapter<AdaptadorNoticias.NoticiasViewHolder>() {

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

        holder.imagen.setImageResource(noticias.imagenID)

        val context = holder.itemView.context

        // Configurar el estado inicial del botón de Like
        holder.btnLike.apply {
            tag = noticias.liked // Establecer el tag inicial
            if (noticias.liked) {
                setImageResource(R.drawable.heart)
                setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                setImageResource(R.drawable.heart_outline)
                setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
            setOnClickListener {
                val isLiked = noticias.liked
                noticias.liked = !isLiked
                val imageButton = it as ImageButton
                if (!isLiked) {
                    imageButton.setImageResource(R.drawable.heart)
                    imageButton.setColorFilter(ContextCompat.getColor(context, R.color.red))
                } else {
                    imageButton.setImageResource(R.drawable.heart_outline)
                    imageButton.setColorFilter(ContextCompat.getColor(context, R.color.gray))
                }
                // Aquí podrías llamar a una función para actualizar el estado "liked" de la noticia
                // si lo estás gestionando fuera del Adapter.
            }
        }

        // Configurar el estado inicial del botón de Guardar
        holder.btnSave.apply {
            if (noticias.saved) {
                setImageResource(R.drawable.bookmark)
                setColorFilter(ContextCompat.getColor(context, R.color.blue))
            } else {
                setImageResource(R.drawable.bookmark_outline)
                setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
            // Acciones al hacer click en el botón guardar
            setOnClickListener {
                val isSaved = noticias.saved
                noticias.saved = !isSaved
                onGuardarClicked(noticias)
                val imageButton = it as ImageButton
                if (!isSaved) {
                    imageButton.setImageResource(R.drawable.bookmark)
                    imageButton.setColorFilter(ContextCompat.getColor(context, R.color.blue))
                } else {
                    imageButton.setImageResource(R.drawable.bookmark_outline)
                    imageButton.setColorFilter(ContextCompat.getColor(context, R.color.gray))
                }
            }
        }
    }

    override fun getItemCount() = newsList.size
}