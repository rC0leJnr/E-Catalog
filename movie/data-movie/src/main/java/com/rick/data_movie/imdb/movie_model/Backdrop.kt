package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class BackdropDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("aspectRatio")
    val aspectRatio: Double,
    @SerializedName("language")
    val language: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
)

@Parcelize
data class Backdrop(
    @SerializedName("id")
    val id: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("language")
    val language: String,
) : Parcelable