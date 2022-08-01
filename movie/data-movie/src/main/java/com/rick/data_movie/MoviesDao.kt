package com.rick.data_movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movie: List<Result>)

    @Query("DELETE FROM movies_db")
    suspend fun clearMovies()

    @Query("SELECT * FROM movies_db ORDER BY id ASC")
    suspend fun getMovies(): List<Result>

//    @Query("SELECT * FROM repos WHERE " +
//            "name LIKE :queryString OR description LIKE :queryString " +
//            "ORDER BY stars DESC, name ASC")
//    fun reposByName(queryString: String): PagingSource<Int, Repo>

}