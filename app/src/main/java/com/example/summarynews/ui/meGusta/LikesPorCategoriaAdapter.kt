package com.example.summarynews.ui.meGusta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.summarynews.R

/**
 * Adaptador para un [RecyclerView] que muestra la cantidad de "me gusta" por categoría.
 *
 * Este adaptador obtiene una lista de pares (categoría, número de likes) y los presenta
 * en elementos individuales dentro de un RecyclerView, mostrando el nombre de la categoría
 * y la cantidad de "me gusta" asociados a ella.
 *
 * @property likesPorCategoria La lista de pares donde cada par representa una categoría ([String])
 * y el número de "me gusta" ([Int]) que ha recibido.
 */
class LikesPorCategoriaAdapter(private var likesPorCategoria: List<Pair<String, Int>>) :
    RecyclerView.Adapter<LikesPorCategoriaAdapter.ViewHolder>() {

    /**
     * ViewHolder para los elementos individuales de la lista.
     *
     * Contiene las referencias a las vistas dentro del layout `item_likes_por_categoria.xml`.
     *
     * @param itemView La vista del elemento individual del RecyclerView.
     */
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

    /**
     * Devuelve el número total de elementos en el conjunto de datos que el adaptador gestiona.
     *
     * @return El número total de elementos.
     */
    override fun getItemCount(): Int = likesPorCategoria.size

    /**
     * Actualiza la lista de "me gusta" por categoría y notifica al adaptador sobre el cambio
     * para que el RecyclerView refleje los cambios en el momento.
     *
     * @param nuevaLista La nueva lista de pares (categoría, número de likes) para mostrar.
     */
    fun actualizarLista(nuevaLista: List<Pair<String, Int>>) {
        likesPorCategoria = nuevaLista
        notifyDataSetChanged()
    }
}