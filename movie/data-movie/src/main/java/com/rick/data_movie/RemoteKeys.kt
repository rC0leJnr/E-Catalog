package com.rick.data_movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val movieId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
