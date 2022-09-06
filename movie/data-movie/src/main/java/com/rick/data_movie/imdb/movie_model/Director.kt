package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DirectorDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
) : Parcelable