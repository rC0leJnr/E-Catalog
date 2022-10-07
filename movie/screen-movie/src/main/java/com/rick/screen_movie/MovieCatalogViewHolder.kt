package com.rick.screen_movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_movie.ny_times.Movie
import com.rick.screen_movie.databinding.MovieEntryBinding
import com.rick.screen_movie.util.provideGlide

class MovieCatalogViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: Movie) -> Unit
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    private val title = binding.movieName
    private val rating = binding.movieRating
    private val image = binding.movieImage
    private val summary = binding.movieSummary

    private lateinit var movie: Movie

    init {
        binding.root.setOnClickListener(this)
    }

    fun bind(movie: Movie) {
        this.movie = movie
        this.title.text = movie.title
        this.summary.text = movie.summary
        val resources = itemView.resources
        this.rating.text =
            if (movie.rating.isNotBlank()) resources.getString(R.string.rated, movie.rating)
            else resources.getString(R.string.no_data)
        val src = movie.multimedia.src
        if (src.isNotBlank()) provideGlide(this.image, src)
    }

    override fun onClick(v: View) {
        onItemClicked(v, movie)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: Movie) -> Unit
        ): MovieCatalogViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return MovieCatalogViewHolder(
                itemBinding,
                onItemClicked
            )
        }
    }
}