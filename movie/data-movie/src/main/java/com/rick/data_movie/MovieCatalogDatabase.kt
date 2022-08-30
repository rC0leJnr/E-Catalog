package com.rick.data_movie

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rick.data_movie.imdb.SearchedMovieDao
import com.rick.data_movie.imdb.search_model.SearchedMovie
import com.rick.data_movie.ny_times.Movie

@Database(
    entities = [Movie::class, SearchedMovie::class, RemoteKeys::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MovieCatalogDatabase: RoomDatabase() {
    abstract val searchedMoviesDao: SearchedMovieDao
    abstract val moviesDao: MoviesDao
    abstract val remoteKeysDao: RemoteKeysDao

    companion object {
        const val DATABASE_NAME = "MOVIE_DB"
    }
}