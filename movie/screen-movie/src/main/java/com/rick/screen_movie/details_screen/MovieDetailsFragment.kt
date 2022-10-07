package com.rick.screen_movie.details_screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.screen_movie.R
import com.rick.screen_movie.databinding.FragmentMovieDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

    private var _binding: FragmentMovieDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var imagesAdapter: DetailsImagesAdapter
    private lateinit var actorsAdapter: ActorDetailsAdapter
    private lateinit var similarsAdapter: SimilarDetailsAdapter

    private var series: String? = null
    private var title: String? = null
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            fadeMode = MaterialContainerTransform.FADE_MODE_CROSS
            duration = resources.getInteger(R.integer.catalog_motion_duration_long).toLong()
            scrimColor = Color.TRANSPARENT
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)

        arguments?.let {
            val safeArgs = MovieDetailsFragmentArgs.fromBundle(it)
            series = safeArgs.series
            title = safeArgs.movieTitle
            id = safeArgs.movieId
        }

        title?.let { viewModel.getMovieOrSeriesId(it) }
        id?.let { viewModel.getMovieOrSeries(it) }
        series?.let { viewModel.getMovieOrSeries(it) }

        initAdapters()

        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }

        binding.bindState(
            viewModel.movingPictures, viewModel.searchLoading, viewModel.searchError
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailsCardView.transitionName = getString(R.string.movie_detail_transition_name)
        binding.root.transitionName = getString(R.string.search_transition_name, id)
    }

    private fun initAdapters() {
        imagesAdapter = DetailsImagesAdapter()
        actorsAdapter = ActorDetailsAdapter()
        similarsAdapter = SimilarDetailsAdapter()
    }

    private fun FragmentMovieDetailsBinding.bindState(
        movie: LiveData<IMDBMovie>, loading: LiveData<Boolean>, error: LiveData<String>
    ) {
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager2 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val layoutManager3 =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        listImages.layoutManager = layoutManager
        listActors.layoutManager = layoutManager2
        listSimilars.layoutManager = layoutManager3
        listImages.adapter = imagesAdapter
        listActors.adapter = actorsAdapter
        listSimilars.adapter = similarsAdapter

        bindList(
            imagesAdapter, actorsAdapter, similarsAdapter, movie, loading, error
        )
    }

    private fun FragmentMovieDetailsBinding.bindList(
        imagesAdapter: DetailsImagesAdapter,
        actorDetailsAdapter: ActorDetailsAdapter,
        similarDetailsAdapter: SimilarDetailsAdapter,
        movie: LiveData<IMDBMovie>,
        loading: LiveData<Boolean>,
        error: LiveData<String>
    ) {

        val noData = getString(R.string.no_data)

        loading.observe(viewLifecycleOwner) { progressing ->
            if (progressing) {
                detailsProgressBar.visibility = View.VISIBLE
            } else detailsProgressBar.visibility = View.GONE
        }

        error.observe(viewLifecycleOwner) { msg ->
            if (msg.isNullOrBlank()) detailsErrorMessage.visibility = View.GONE
            else detailsErrorMessage.visibility = View.VISIBLE
            movieGenres.text = resources.getString(R.string.genres, noData)

            movieAwards.text = resources.getString(R.string.awards, noData)
            moviePublicationDate.text = resources.getString(
                R.string.release_date, noData
            )
            movieRuntime.text = resources.getString(
                R.string.runtime, noData
            )
            imdbChip.text = resources.getString(
                R.string.imdb_rating, noData
            )
            rTomatoesChip.text =
                resources.getString(R.string.tomato_rating, getString(R.string.no_data))
            movieDbChip.text = resources.getString(
                R.string.db_rating, noData
            )
            movieBudget.text = resources.getString(
                R.string.budget, noData
            )
            movieOpenWeekendGross.text = resources.getString(R.string.open_week_gross, noData)

            movieWorldWideGross.text = resources.getString(
                R.string.world_wide_gross, noData
            )
        }

        movie.observe(viewLifecycleOwner) { imdb: IMDBMovie ->
            movieTitle.text = imdb.title
            movieSummary.text = imdb.plot
            movieGenres.text =
                if (imdb.genres.isNotEmpty()) resources.getString(R.string.genres, imdb.genres)
                else resources.getString(
                    R.string.genres, noData
                )

            movieAwards.text =
                if (imdb.awards.isNotBlank()) resources.getString(R.string.awards, imdb.awards)
                else resources.getString(
                    R.string.awards, noData
                )
            moviePublicationDate.text =
                if (imdb.releaseDate.isNotEmpty()) resources.getString(
                    R.string.release_date, imdb.releaseDate
                )
                else resources.getString(
                    R.string.release_date, noData
                )
            movieRuntime.text =
                if (!imdb.runtimeStr.isNullOrEmpty()) resources.getString(
                    R.string.runtime, imdb.runtimeStr
                )
                else resources.getString(
                    R.string.runtime, noData
                )
            imdbChip.text =
                if (imdb.ratings.imDb.isNotEmpty()) resources.getString(
                    R.string.imdb_rating, imdb.ratings.imDb
                )
                else resources.getString(
                    R.string.imdb_rating, noData
                )
            rTomatoesChip.text =
                if (imdb.ratings.rottenTomatoes.isNotEmpty()) resources.getString(
                    R.string.tomato_rating, imdb.ratings.rottenTomatoes
                )
                else resources.getString(
                    R.string.tomato_rating, noData
                )
            movieDbChip.text =
                if (imdb.ratings.theMovieDb.isNotEmpty()) resources.getString(
                    R.string.db_rating, imdb.ratings.theMovieDb
                ) else resources.getString(
                    R.string.db_rating, noData
                )
            movieBudget.text =
                if (imdb.boxOffice.budget.isNotEmpty()) resources.getString(
                    R.string.budget, imdb.boxOffice.budget
                )
                else resources.getString(
                    R.string.budget, noData
                )
            movieOpenWeekendGross.text =
                if (imdb.boxOffice.openingWeekendUSA.isNotEmpty()) resources.getString(
                    R.string.open_week_gross, imdb.boxOffice.openingWeekendUSA
                )
                else resources.getString(
                    R.string.open_week_gross, noData
                )
            movieWorldWideGross.text =
                if (imdb.boxOffice.cumulativeWorldwideGross.isNotEmpty()) resources.getString(
                    R.string.world_wide_gross, imdb.boxOffice.cumulativeWorldwideGross
                )
                else resources.getString(
                    R.string.world_wide_gross, noData
                )
            imagesAdapter.imagesDiffer.submitList(imdb.images.items)
            actorDetailsAdapter.actorsDiffer.submitList(imdb.actorList)
            similarDetailsAdapter.similarsDiffer.submitList(imdb.similars)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

//val dummyImages = listOf(
//    Item(
//        "https://image.tmdb.org/t/p/original/8IB2e4r4oVhHnANbnm7O3Tj6tF8.jpg"
//    ),
//    Item(
//        "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg"
//    ),
//    Item(
//        "https://image.tmdb.org/t/p/original/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg"
//    )
//)
//val dummyActors = listOf(
//    Actor(
//        "nm0000138",
//        "https://m.media-amazon.com/images/M/MV5BMjI0MTg3MzI0M15BMl5BanBnXkFtZTcwMzQyODU2Mw@@._V1_Ratio0.7273_AL_.jpg",
//        "Leonardo DiCaprio",
//        "Cobb"
//    ),
//    Actor(
//        "nm0330687",
//        "https://m.media-amazon.com/images/M/MV5BMTY3NTk0NDI3Ml5BMl5BanBnXkFtZTgwNDA3NjY0MjE@._V1_Ratio0.7273_AL_.jpg",
//        "Joseph Gordon-Levitt",
//        "Arthur"
//    ),
//    Actor(
//        "nm0680983",
//        "https://m.media-amazon.com/images/M/MV5BNmNhZmFjM2ItNTlkNi00ZTQ4LTk3NzYtYTgwNTJiMTg4OWQzXkEyXkFqcGdeQXVyMTM1MjAxMDc3._V1_Ratio0.7273_AL_.jpg",
//        "Elliot Page",
//        "Ariadne (as Ellen Page)"
//    )
//)
//val dummySimilars = listOf(
//    Similar(
//        "tt0816692",
//        "Interstellar",
//        "https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_Ratio0.6763_AL_.jpg",
//        "8.6"
//    ),
//    Similar(
//        "tt0468569",
//        "The Dark Knight",
//        "https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_Ratio0.6763_AL_.jpg",
//        "9.0"
//    ),
//    Similar(
//        "tt0137523",
//        "Fight Club",
//        "https://m.media-amazon.com/images/M/MV5BNDIzNDU0YzEtYzE5Ni00ZjlkLTk5ZjgtNjM3NWE4YzA3Nzk3XkEyXkFqcGdeQXVyMjUzOTY1NTc@._V1_Ratio0.6763_AL_.jpg",
//        "8.8"
//    )
//)
