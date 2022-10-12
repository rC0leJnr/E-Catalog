package com.rick.screen_anime

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rick.data_anime.model_anime.Anime
import com.rick.screen_anime.databinding.AnimeEntryBinding
import com.rick.screen_anime.manga_screen.provideGlide

class AnimeViewHolder(
    binding: AnimeEntryBinding,
    private val onItemClick: (view: View, anime: Anime) -> Unit
) : RecyclerView.ViewHolder(binding.root),
    View.OnClickListener {
    private val title = binding.title
    private val image = binding.image
    private val synopsis = binding.synopsis
    private val pgRating = binding.pgRating

    private lateinit var anime: Anime

    fun bind(anime: Anime) {
        this.anime = anime
        val resources = itemView.resources
        this.title.text = anime.title
        anime.images.jpg.imageUrl.let { provideGlide(this.image, it) }
        this.synopsis.text = anime.synopsis
        this.pgRating.text = resources.getString(R.string.pg_rating, anime.rating)
    }


    override fun onClick(view: View) {
        onItemClick(view, anime)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClick: (view: View, anime: Anime) -> Unit
        ): AnimeViewHolder {
            val itemBinding =
                AnimeEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return AnimeViewHolder(itemBinding, onItemClick)
        }
    }

}
