package com.rick.data_book.favorite

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "favorite")
data class Favorite(
    @PrimaryKey(autoGenerate = true) @SerializedName("id") val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("author")
    val author: String
): Parcelable