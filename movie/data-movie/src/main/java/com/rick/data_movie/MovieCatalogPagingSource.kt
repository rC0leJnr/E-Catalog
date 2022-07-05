package com.rick.data_movie

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import java.io.IOException
import java.lang.Integer.max

private const val STARTING_PAGE = 1
private const val LOAD_DELAY_MILLIS = 3_000L
private var ITEMS_PER_PAGE = 7

class MovieCatalogPagingSource(
    private val api: MovieCatalogApi,
    db: MovieCatalogDatabase,
) : PagingSource<Int, ResultDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ResultDto> {
        // Start paging with the starting_key if this is the first load
        val position = params.key ?: STARTING_PAGE
        var movies = mutableListOf<ResultDto>()
        return try {
            ITEMS_PER_PAGE+=20
            movies = mutableListOf()
            val response = api.fetchMovieCatalog(ITEMS_PER_PAGE)
            val result = response.results.toMutableList()
            Log.d("taggoo", "result -> $result")
            movies.addAll(result)
            val nextKey = if (movies.isEmpty()) null
            else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (ITEMS_PER_PAGE /params.loadSize)
            }
            LoadResult.Page(
                data = movies.toList(),
                prevKey = if (position == STARTING_PAGE) null else position - 1,
                nextKey = nextKey
            )
        }
        catch (e: IOException) {
            return LoadResult.Error(e)
        }
        catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ResultDto>): Int? {
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    /**
     * Makes sure the paging key is never less than [STARTING_KEY]
     */
//    private fun ensureValidKey(key: Int) = max(STARTING_KEY, key)
}