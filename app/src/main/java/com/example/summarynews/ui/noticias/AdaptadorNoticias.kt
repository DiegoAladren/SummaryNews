package com.example.summarynews.ui.noticias

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.summarynews.R
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.summarynews.db.NoticiaEntity
import androidx.core.net.toUri

class AdaptadorNoticias(
    private var newsList: List<NoticiaEntity>,
    private val onLikeClicked: (NoticiaEntity) -> Unit,
    private val onSaveClicked: (NoticiaEntity) -> Unit,
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
        val noticia = newsList[position]
        holder.titulo.text = noticia.titulo
        holder.resumen.text = noticia.resumen
        // Cargar la imagen
        if (!noticia.imagenURL.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(noticia.imagenURL)
                .placeholder(R.drawable.placeholder_image) // Imagen de carga
                .error(R.drawable.placeholder_image) // Imagen de error si falla la carga
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(noticia.imagenID)
        }

        val context = holder.itemView.context
        // Enlace a la noticia original
        val tvLink: TextView = holder.itemView.findViewById(R.id.tvLink)
        tvLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = noticia.fuenteURL.toUri()
            }
            context.startActivity(intent)
        }

        // Configurar el estado inicial del botón de Like
        holder.btnLike.apply {
            tag = noticia.liked // Establecer el tag inicial
            if (noticia.liked) {
                setImageResource(R.drawable.heart)
                setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                setImageResource(R.drawable.heart_outline)
                setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
            // Detección de Like
            setOnClickListener {
                val isLiked = noticia.liked
                val nuevaNoticia = noticia.copy(liked = !isLiked)
                onLikeClicked(nuevaNoticia)
                // Actualizar la UI localmente sin recargar la lista completa
                if (!isLiked) {
                    setImageResource(R.drawable.heart)
                    setColorFilter(ContextCompat.getColor(context, R.color.red))
                } else {
                    setImageResource(R.drawable.heart_outline)
                    setColorFilter(ContextCompat.getColor(context, R.color.gray))
                }
                // No es necesario llamar a notifyItemChanged aquí porque LiveData actualizará la lista
            }
        }

        // Configurar el estado inicial del botón de Guardar
        holder.btnSave.apply {
            if (noticia.saved) {
                setImageResource(R.drawable.bookmark)
                setColorFilter(ContextCompat.getColor(context, R.color.blue))
            } else {
                setImageResource(R.drawable.bookmark_outline)
                setColorFilter(ContextCompat.getColor(context, R.color.gray))
            }
            // Acciones al hacer click en el botón guardar
            setOnClickListener {
                val isSaved = noticia.saved
                val nuevaNoticia = noticia.copy(saved = !isSaved)
                onSaveClicked(nuevaNoticia)
                // Actualizar la UI localmente sin recargar la lista completa
                if (!isSaved) {
                    setImageResource(R.drawable.bookmark)
                    setColorFilter(ContextCompat.getColor(context, R.color.blue))
                } else {
                    setImageResource(R.drawable.bookmark_outline)
                    setColorFilter(ContextCompat.getColor(context, R.color.gray))
                }
                // No es necesario llamar a notifyItemChanged aquí porque LiveData actualizará la lista
            }
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    fun actualizarLista(nuevaLista: List<NoticiaEntity>) {
        newsList = nuevaLista
        notifyDataSetChanged()
    }
}