package com.rick.screen_anime.search_screen

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.data_anime.model_anime.Anime
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentSearchAnimeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class SearchAnimeFragment : Fragment() {

    private var _binding: FragmentSearchAnimeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchAnimeViewModel by viewModels()
    private lateinit var adapter: SearchAnimeAdapter

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
    ): View? {
        _binding = FragmentSearchAnimeBinding.inflate(inflater, container, false)

        binding.toolbar.apply {
            inflateMenu(R.menu.jikan_menu)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.search_jikan -> {
                        binding.updateListFromInput(viewModel.searchUiAction)
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
            adapter = adapter,
            searchList = viewModel.searchAnimeList,
            searchLoading = viewModel.searchLoading,
            searchError = viewModel.searchError,
            uiAction = viewModel.searchUiAction,
            uiState = viewModel.searchUiState
        )

        return binding.root
    }

    private fun initAdapter() {
        adapter = SearchAnimeAdapter(
            this::onAnimeClick
        )

        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)
        binding.list.itemAnimator = DefaultItemAnimator()
    }

    private fun FragmentSearchAnimeBinding.bindState(
        adapter: SearchAnimeAdapter,
        searchList: LiveData<List<Anime>>,
        searchLoading: LiveData<Boolean>,
        searchError: LiveData<String>,
        uiAction: (SearchUiAction) -> Unit,
        uiState: StateFlow<SearchUiState>,
    ) {
        list.adapter = adapter

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiAction
        )

        bindList(
            adapter = adapter,
            searchList = searchList,
            searchLoading = searchLoading,
            searchError = searchError,
        )
    }

    private fun FragmentSearchAnimeBinding.bindSearch(
        uiState: StateFlow<SearchUiState>,
        onQueryChanged: (SearchUiAction) -> Unit
    ) {
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

        lifecycleScope.launchWhenCreated {
            uiState
                .map { it.animeQuery }
                .distinctUntilChanged()
                .collectLatest(searchInput::setText)
        }
    }
    private fun FragmentSearchAnimeBinding.updateListFromInput(onQueryChanged: (SearchUiAction.SearchAnime) -> Unit) {
        searchInput.text!!.trim().let { query ->
            if (query.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(SearchUiAction.SearchBooks(query = query.toString()))

            }
        }
    }

    private fun FragmentSearchAnimeBinding.bindList(
        adapter: SearchAnimeAdapter,
        searchList: LiveData<List<Anime>>,
        searchLoading: LiveData<Boolean>,
        searchError: LiveData<String>
    ) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}