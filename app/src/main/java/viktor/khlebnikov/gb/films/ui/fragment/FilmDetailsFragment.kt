package viktor.khlebnikov.gb.films.ui.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import viktor.khlebnikov.gb.films.R
import viktor.khlebnikov.gb.films.api.ApiService
import viktor.khlebnikov.gb.films.databinding.FragmentFilmDetailsBinding

@AndroidEntryPoint
class FilmDetailsFragment : Fragment(R.layout.fragment_film_details) {

    private var _binding: FragmentFilmDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<FilmDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentFilmDetailsBinding.bind(view)

        binding.apply {
            val film = args.film
            Glide.with(this@FilmDetailsFragment)
                .load("${ApiService.IMAGE_BASE_URL}${film.posterPath}")
                .error(R.drawable.ic_image_empty)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean,
                    ): Boolean {
                        progressBar.isVisible = false
                        tvMovieTitle.isVisible = true
                        tvFilmDescription.isVisible = true
                        return false
                    }
                })
                .into(imageViewMoviePoster)

            tvFilmDescription.text = film.overview
            tvMovieTitle.text = film.originalTitle
        }

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}