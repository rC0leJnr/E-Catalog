package com.rick.screen_movie.search_screen

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.tmdb.search.Search
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentSearchBinding
import com.rick.screen_movie.databinding.MovieEntryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            inflateMenu(R.menu.search_menu)

            menu.findItem(R.id.fav_imdb).isVisible = false

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search_imdb -> {
                        binding.updateListFromInput(viewModel.searchAction)
                        true
                    }

                    else -> super.onOptionsItemSelected(item)
                }
            }

            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        initAdapter()

        binding.bindState(
            searchList = viewModel.searchList,
            searchLoading = viewModel.searchLoading,
            searchError = viewModel.searchError,
            uiAction = viewModel.searchAction,
            uiState = viewModel.searchState,
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun initAdapter() {
        val circularProgressDrawable = CircularProgressDrawable(requireContext()).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
        val options = RequestOptions().placeholder(circularProgressDrawable)
        val glide = Glide.with(requireContext())
        searchAdapter = SearchAdapter(
            glide,
            options,
            this::onMovieClick,
            this::onFavClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
    }

    private fun FragmentSearchBinding.bindState(
        searchList: LiveData<SearchUiState.Response>,
        searchLoading: LiveData<SearchUiState.Loading>,
        searchError: LiveData<SearchUiState.Error>,
        uiAction: (SearchUiAction) -> Unit,
        uiState: StateFlow<SearchUiState.Query>
    ) {

        list.adapter = searchAdapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiAction
        )

        bindList(
            adapter = searchAdapter,
            searchList = searchList,
            searchLoading = searchLoading,
            searchError = searchError,
        )
    }

    private fun FragmentSearchBinding.bindSearch(
        uiState: StateFlow<SearchUiState.Query>,
        onQueryChanged: (SearchUiAction) -> Unit,
    ) {

//        showSoftKeyboard(searchInput, requireContext())

        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchInput.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collectLatest(searchInput::setText)
        }
    }

    private fun FragmentSearchBinding.updateListFromInput(onQueryChanged: (SearchUiAction.Search) -> Unit) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.Search(query = query.toString()))

            }
        }
    }

    private fun FragmentSearchBinding.bindList(
        adapter: SearchAdapter,
        searchList: LiveData<SearchUiState.Response>,
        searchLoading: LiveData<SearchUiState.Loading>,
        searchError: LiveData<SearchUiState.Error>
    ) {
        lifecycleScope.launch {
            searchList.observe(viewLifecycleOwner) {
                adapter.searchDiffer.submitList(it.response)
            }
        }

        lifecycleScope.launch {
            searchLoading.observe(viewLifecycleOwner) {
                if (it.loading) searchProgressBar.visibility = View.VISIBLE
                else searchProgressBar.visibility = View.GONE
            }

            searchError.observe(viewLifecycleOwner) {
                if (it.msg != null ) {
                    searchErrorMessage.visibility = View.VISIBLE
                } else {
                    searchErrorMessage.visibility = View.GONE
                }
            }
        }
    }

    //TODO (REMOVE .toString())
    private fun onMovieClick(view: View, movie: Search) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
        }
        val searchToDetails = getString(R.string.search_transition_name, movie.id.toString())
        val extras = FragmentNavigatorExtras(view to searchToDetails)
        val action =
            SearchFragmentDirections
                .actionSearchFragmentToDetailsFragment(
                    movieId = movie.id.toString(),
                    movieTitle = null,
                    series = null
                )

        findNavController().navigate(directions = action, navigatorExtras = extras)
    }

    private fun onFavClick(favorite: Favorite) {
        viewModel.onEvent(SearchUiAction.InsertFavorite(favorite))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class SearchAdapter(
    private val glide: RequestManager,
    private val options: RequestOptions,
    private val onItemClicked: (view: View, movie: Search) -> Unit,
    private val onFavClicked: (favorite: Favorite) -> Unit
) : RecyclerView.Adapter<SearchViewHolder>() {

    private val searchDiffUtil = object : DiffUtil.ItemCallback<Search>() {
        override fun areItemsTheSame(
            oldItem: Search,
            newItem: Search
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Search,
            newItem: Search
        ): Boolean {
            return oldItem == newItem
        }
    }

    val searchDiffer = AsyncListDiffer(this, searchDiffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder.create(parent, onItemClicked, onFavClicked)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val searchResult = searchDiffer.currentList[position]
        (holder).bind(glide, options, searchResult)
    }

    override fun getItemCount(): Int =
        searchDiffer.currentList.size
}

class SearchViewHolder(
    binding: MovieEntryBinding,
    private val onItemClicked: (view: View, movie: Search) -> Unit,
    private val onFavClicked: (favorite: Favorite) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val image = binding.movieImage
    private val title = binding.movieName
    private val description = binding.movieSummary
    private val rootLayout = binding.movieEntryCardView

    init {
        binding.root.setOnClickListener {
            onItemClicked(it, result)
        }
        binding.favButton.setOnClickListener {
//            onFavClicked(result.toFavorite()) TODO (code fav function)
        }
    }

    private lateinit var result: Search

    fun bind(glide: RequestManager, options: RequestOptions, searchResult: Search) {
        this.rootLayout.transitionName = "search ${searchResult.id}"
        this.result = searchResult
        this.title.text = searchResult.title
        this.description.text = searchResult.overview
        glide
            .load(searchResult.backdropPath) //TODO, add path to proper url
            .apply(options)
            .into(this.image)
    }

//    overridee fun onClick(v: View) {
//        onItemClicked(v, searchResult)
//    }

    companion object {
        fun create(
            parent: ViewGroup,
            onItemClicked: (view: View, movie: Search) -> Unit,
            onFavClicked: (favorite: Favorite) -> Unit
        ): SearchViewHolder {
            val itemBinding = MovieEntryBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(itemBinding, onItemClicked, onFavClicked)
        }
    }
}