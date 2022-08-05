package com.rick.data_movie

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val title: String,
    val summary: String,
    val rating: String,
    val openingDate: String?,
    val link: Link,
    val multimedia: Multimedia,
): Parcelable
