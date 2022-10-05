package com.rick.screen_movie.search_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.core.Resource
import com.rick.data_movie.MovieCatalogRepository
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MovieCatalogRepository
) : ViewModel() {

    private val imdbKey: String

    private val _searchList: MutableLiveData<List<IMDBSearchResult>> by
    lazy { MutableLiveData<List<IMDBSearchResult>>() }
    val searchList: LiveData<List<IMDBSearchResult>> get() = _searchList

    private val _searchLoading: MutableLiveData<Boolean> by
    lazy { MutableLiveData<Boolean>(false) }
    val searchLoading: LiveData<Boolean> get() = _searchLoading

    private val _searchError: MutableLiveData<String> by
    lazy { MutableLiveData<String>() }
    val searchError: LiveData<String> get() = _searchError

    val searchState: StateFlow<SearchUiState>
    val searchAction: (SearchUiAction) -> Unit

    init {

        // Load api_keys
        System.loadLibrary("movie-keys")
        imdbKey = getIMDBKey()

        val actionStateFlow = MutableSharedFlow<SearchUiAction>()
        val search =
            actionStateFlow.filterIsInstance<SearchUiAction.Search>().distinctUntilChanged()

        viewModelScope.launch {
            search.collectLatest {
                searchMovies(it.query)
                searchSeries(it.query)
            }
        }

        searchState = search.map { SearchUiState(searchQuery = it.query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000),
                initialValue = SearchUiState()
            )

        searchAction = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }
    }

    private fun searchMovies(title: String) {
        viewModelScope.launch {
            repository.searchMovies(apiKey = imdbKey, query = title).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchList.postValue(result.data!!)
                    }
                }
            }
        }
    }

    private fun searchSeries(title: String) {
        viewModelScope.launch {
            repository.searchSeries(apiKey = imdbKey, query = title).collect { result ->
                when (result) {
                    is Resource.Error -> {
                        _searchError.postValue(result.message)
                    }
                    is Resource.Loading -> {
                        _searchLoading.postValue(result.isLoading)
                    }
                    is Resource.Success -> {
                        _searchList.postValue(result.data!!)
                    }
                }
            }
        }
    }

    private external fun getIMDBKey(): String
}

data class SearchUiState(
    val searchQuery: String? = null,
)

sealed class SearchUiAction {
    data class Search(val query: String) : SearchUiAction()
}