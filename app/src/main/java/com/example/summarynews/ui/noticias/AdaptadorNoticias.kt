package com.example.summarynews.ui.noticias

import android.content.Context
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

/**
 * [AdaptadorNoticias] es un adaptador para [RecyclerView] que se encarga de mostrar una lista de [NoticiaEntity].
 * Proporciona la lógica para enlazar los datos de las noticias con las vistas individuales de cada elemento en la lista,
 * incluyendo la carga de imágenes, la gestión de clics en los botones de "me gusta" y "guardar", y la opción de abrir el enlace
 * original de cada noticia en un navegador externo.
 *
 * @property newsList La lista de objetos [NoticiaEntity] que se mostrarán en el RecyclerView.
 * @property onLikeClicked Una función lambda que se invoca cuando una noticia es marcada como "me gusta".
 * Recibe la [NoticiaEntity] actualizada como parámetro.
 * @property onSaveClicked Una función lambda que se invoca cuando una noticia es marcada como "guardada".
 * Recibe la [NoticiaEntity] actualizada como parámetro.
 */
class AdaptadorNoticias(
    private var newsList: List<NoticiaEntity>,
    private val onLikeClicked: (NoticiaEntity) -> Unit,
    private val onSaveClicked: (NoticiaEntity) -> Unit,
) : RecyclerView.Adapter<AdaptadorNoticias.NoticiasViewHolder>() {

    /**
     * [NoticiasViewHolder] es una clase interna que representa cada elemento individual de la lista (fila)
     * en el [RecyclerView]. Contiene las referencias a las vistas dentro del layout `item_news.xml`.
     *
     * @param itemView La vista raíz del elemento de la lista.
     */
    class NoticiasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.tvTitle)
        val resumen: TextView = itemView.findViewById(R.id.tvSummary)
        val imagen: ImageView = itemView.findViewById(R.id.ivNewsImage)
        val btnLike: ImageButton = itemView.findViewById(R.id.likeButton)
        val btnSave: ImageButton = itemView.findViewById(R.id.saveButton)
    }

    /**
     * Se llama cuando el [RecyclerView] necesita un nuevo [NoticiasViewHolder] para representar un elemento.
     * Infla el layout `item_news.xml` y devuelve una nueva instancia de [NoticiasViewHolder].
     *
     * @param parent El [ViewGroup] al que se adjuntará la nueva vista después de ser inflada.
     * @param viewType El tipo de vista del nuevo View.
     * @return Una nueva instancia de [NoticiasViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticiasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NoticiasViewHolder(view)
    }

    /**
     * Se llama por el [RecyclerView] para mostrar los datos en la posición especificada.
     * Este método actualiza el contenido del [NoticiasViewHolder] para reflejar el elemento en la posición dada.
     *
     * @param holder El [NoticiasViewHolder] que debe ser actualizado para representar el contenido del elemento en la posición dada.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: NoticiasViewHolder, position: Int) {
        val noticia = newsList[position]
        holder.titulo.text = noticia.titulo
        holder.resumen.text = noticia.resumen

        configuracionCargarImagenes(noticia, holder)

        val context = holder.itemView.context

        configuracionEnlaceOriginal(holder, noticia, context)

        configuracionLikes(holder, noticia, context)

        configuracionGuardados(holder, noticia, context)
    }

    /**
     * Configura la carga de imágenes para la noticia. Si [NoticiaEntity.imagenURL] no es nula ni vacía,
     * utiliza Glide para cargar la imagen desde la URL. De lo contrario, usa [NoticiaEntity.imagenID]
     * para cargar una imagen local (placeholder).
     *
     * @param noticia La [NoticiaEntity] que contiene la información de la imagen.
     * @param holder El [NoticiasViewHolder] que contiene el [ImageView] donde se mostrará la imagen.
     */
    private fun configuracionCargarImagenes(
        noticia: NoticiaEntity,
        holder: NoticiasViewHolder,
    ) {
        if (!noticia.imagenURL.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(noticia.imagenURL)
                .placeholder(R.drawable.placeholder_image) // Imagen de carga
                .error(R.drawable.placeholder_image) // Imagen de error si falla la carga
                .into(holder.imagen)
        } else {
            holder.imagen.setImageResource(noticia.imagenID)
        }
    }

    /**
     * Configura el comportamiento de clic para el TextView que muestra el enlace original de la noticia.
     * Al hacer clic, se abre el enlace en un navegador web utilizando un [Intent.ACTION_VIEW].
     *
     * @param holder El [NoticiasViewHolder] que contiene el TextView del enlace.
     * @param noticia La [NoticiaEntity] que contiene la URL original.
     * @param context El contexto utilizado para iniciar la actividad del Intent.
     */
    private fun configuracionEnlaceOriginal(
        holder: NoticiasViewHolder,
        noticia: NoticiaEntity,
        context: Context?,
    ) {
        val tvLink: TextView = holder.itemView.findViewById(R.id.tvLink)
        tvLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = noticia.fuenteURL.toUri()
            }
            context?.startActivity(intent)
        }
    }

    /**
     * Configura el botón de "me gusta" para cada noticia.
     * Actualiza la imagen y el color del botón según el estado de `liked` de la noticia.
     * Cuando se hace clic, invoca la lambda `onLikeClicked` con la noticia actualizada.
     *
     * @param holder El [NoticiasViewHolder] que contiene el botón de "me gusta".
     * @param noticia La [NoticiaEntity] actual.
     * @param context El contexto utilizado para obtener los recursos de color.
     */
    private fun configuracionLikes(
        holder: NoticiasViewHolder,
        noticia: NoticiaEntity,
        context: Context,
    ) {
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
    }

    /**
     * Configura el botón de "guardar" para cada noticia.
     * Actualiza la imagen y el color del botón según el estado de `saved` de la noticia.
     * Cuando se hace clic, invoca la lambda `onSaveClicked` con la noticia actualizada.
     *
     * @param holder El [NoticiasViewHolder] que contiene el botón de "guardar".
     * @param noticia La [NoticiaEntity] actual.
     * @param context El contexto utilizado para obtener los recursos de color.
     */
    private fun configuracionGuardados(
        holder: NoticiasViewHolder,
        noticia: NoticiaEntity,
        context: Context,
    ) {
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

    /**
     * Devuelve el número total de elementos en el conjunto de datos que el adaptador gestiona.
     *
     * @return El número de noticias en la lista.
     */
    override fun getItemCount(): Int {
        return newsList.size
    }

    /**
     * Actualiza la lista de noticias mostradas por el adaptador.
     * Invierte la nueva lista para mostrar las noticias más recientes primero y luego notifica al
     * [RecyclerView] que los datos han cambiado para que se redibuje.
     *
     * @param nuevaLista La nueva lista de [NoticiaEntity] a mostrar.
     */
    fun actualizarLista(nuevaLista: List<NoticiaEntity>) {
        newsList = nuevaLista.reversed()
        notifyDataSetChanged()
    }
}