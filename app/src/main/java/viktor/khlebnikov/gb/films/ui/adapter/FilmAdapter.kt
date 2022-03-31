package viktor.khlebnikov.gb.films.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import viktor.khlebnikov.gb.films.R
import viktor.khlebnikov.gb.films.api.ApiService
import viktor.khlebnikov.gb.films.databinding.ItemFilmBinding
import viktor.khlebnikov.gb.films.model.Film

class FilmAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Film, FilmAdapter.FilmViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder =
        FilmViewHolder(
            ItemFilmBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class FilmViewHolder(private val binding: ItemFilmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }
        }

        fun bind(film: Film) =
            with(binding) {
                Glide.with(itemView)
                    .load("${ApiService.IMAGE_BASE_URL}${film.posterPath}")
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_image_empty)
                    .into(imageViewPoster)
                tvTitle.text = film.originalTitle
                tvDate.text = film.releaseDate
                tvRate.text = film.voteAverage
            }

    }

    interface OnItemClickListener {
        fun onItemClick(film: Film)
    }

    object MovieDiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Film, newItem: Film) = oldItem == newItem
    }

}