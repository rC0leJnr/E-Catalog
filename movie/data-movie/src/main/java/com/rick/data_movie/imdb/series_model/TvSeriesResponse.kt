package com.rick.data_movie.imdb.series_model


import com.google.gson.annotations.SerializedName

data class TvSeriesResponse(
    @SerializedName("items")
    val tvSeries: List<TvSeriesDto>,
    @SerializedName("errorMessage")
    val errorMessage: String
)