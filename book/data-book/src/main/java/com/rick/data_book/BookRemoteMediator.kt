package com.rick.data_book

import GutenbergApi
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data_book.model.Book

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class BookRemoteMediator(
    private val api: GutenbergApi,
    private val db: BookDatabase
): RemoteMediator<Int, Book>() {



    override suspend fun load(loadType: LoadType, state: PagingState<Int, Book>): MediatorResult {
        val page = when(loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
    }

        private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Book>): RemoteKeys? {
            // Get the last page that was retrieved, that contained items.
            // From that last page, get the last item
            return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
                ?.let { movie ->
                    // Get the remote keys of the last item retrieved
                    db.remoteKeysDao.remoteKeysMovieId(movie.title)
                }
        }

        private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Book>): RemoteKeys? {
            // GEt the first page that was retrieved, that contained items.
            // From that first page, get the first item
            return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
                ?.let { movie ->
                    // GEt the remote keys of the first items retrieved
                    db.remoteKeysDao.remoteKeysMovieId(movie.title)
                }
        }

        private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Book>): RemoteKeys? {
            // The paging library is trying to load data after the anchor position
            // Get the item closest to the anchor position
            return state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.title?.let { movie ->
                    db.remoteKeysDao.remoteKeysMovieId(movie)
                }
            }
        }

    }