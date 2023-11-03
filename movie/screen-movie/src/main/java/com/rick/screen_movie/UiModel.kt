package com.rick.screen_movie

import com.rick.data_movie.ny_times_deprecated.Movie

sealed class UiModel {
    data class MovieItem(val movie: Movie): UiModel()
    data class SeparatorItem(val description: String): UiModel()
}

