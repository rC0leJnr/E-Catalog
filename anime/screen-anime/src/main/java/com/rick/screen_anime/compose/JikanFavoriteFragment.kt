package com.rick.screen_anime.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.Fragment
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_anime.model_jikan.Images
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_jikan.Jpg
import com.rick.screen_anime.R
import com.rick.screen_anime.databinding.FragmentJikanFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JikanFavoriteFragment : Fragment() {

    private var _binding: FragmentJikanFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJikanFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            MdcTheme {
                FavScreen(jikans = dummyData)
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

@Composable
fun FavScreen(jikans: List<Jikan>) {
    Scaffold() {
        LazyColumn(modifier = Modifier.padding(it)) {
            items(jikans) { jikan ->
                JikanItem(jikan = jikan)
                Divider(
                    Modifier.height(1.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
fun JikanItem(jikan: Jikan) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .wrapContentHeight(align = Alignment.CenterVertically)
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.fillMaxWidth()) {
            ConstraintLayout(
                modifier = Modifier.fillMaxWidth()
            ) {
                val (title, image) = createRefs()
                Text(
                    text = jikan.title ?: "no title found",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                )
                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.constrainAs(image) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = stringResource(
                            R.string.favorite
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(jikan.images.jpg.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.fav_jikan),
                modifier = Modifier.height(dimensionResource(id = R.dimen.image_height)),
                contentScale = ContentScale.FillHeight,
            )
            Text(
                text = jikan.synopsis
                    ?: ("no synopsis found, maybe i should give u a bit more text, " +
                            "also, i should wrap you and give u eclipses dots"),
                style = MaterialTheme.typography.body1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = jikan.rating ?: "unrated rating", style = MaterialTheme.typography.body2)
        }
    }
}

@Preview
@Composable
fun JikanItemPrev() {
    FavScreen(jikans = dummyData)
}

private val dummyData = listOf(
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://assets-prd.ignimgs.com/2022/08/17/top25animecharacters-blogroll-1660777571580.jpg",
                "",
                ""
            )
        ),
        null,
        "This dude title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "Def got nothing for you, but i'll keep writng so that we have at least on elong text, there's so much typos, and i am so tired i just want to sleep. i love me",
        null,
        null,
        null,
        null,
        null,
    ),
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://cdn.vox-cdn.com/thumbor/xBIBkXiGLcP-kph3pCX61U7RMPY=/0x0:1400x788/1200x800/filters:focal(588x282:812x506)/cdn.vox-cdn.com/uploads/chorus_image/image/70412073/0377c76083423a1414e4001161e0cdffb0b36e1f_760x400.0.png",
                "",
                ""
            )
        ),
        null,
        "This another title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "I do not have a synopsis for you",
        null,
        null,
        null,
        null,
        null,
    ),
    Jikan(
        0,
        "",
        Images(
            Jpg(
                "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
                "",
                ""
            )
        ),
        null,
        "This another title",
        "anime",
        "",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        0.0,
        1,
        1,
        1,
        1,
        1,
        "I may or may not have something for you",
        null,
        null,
        null,
        null,
        null,
    ),
)