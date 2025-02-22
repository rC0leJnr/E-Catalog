package com.rick.screen_book.favorite_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.themeadapter.material.MdcTheme
import com.rick.data_book.favorite.Favorite
import com.rick.data_book.gutenberg.model.Author
import com.rick.data_book.gutenberg.model.Book
import com.rick.data_book.gutenberg.model.Formats
import com.rick.screen_book.R
import com.rick.screen_book.databinding.FragmentBookFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookFavoritesFragment : Fragment() {

    private var _binding: FragmentBookFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BookFavoriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookFavoriteBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            val books = viewModel.books.observeAsState()
            MdcTheme {
                books.value?.let {
                    FavScreen(books = it)
                }
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
fun FavScreen(books: List<Favorite>) {

    // Maybe some day i'll actually start building this :(
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background
    ) {
        LazyColumn(Modifier.padding(it)) {
            items(books) { book ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookItem(book)
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = stringResource(
                                R.string.favorite
                            )
                        )
                    }
                }
                Divider(
                    Modifier.height(1.dp),
                    color = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Composable
fun BookItem(book: Favorite) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(text = book.title, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = book.author, style = MaterialTheme.typography.body2)
        Spacer(modifier = Modifier.height(1.dp))
    }
}

@Preview
@Composable
fun PreviewBookItem() {

}

@Preview
@Composable
fun FavScreenPrev() {
    MaterialTheme {
        FavScreen(
            books = listOf()
        )
    }
}

val dummyBooks = listOf(
    Book(
        0,
        "Book 1",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf(),
        true
    ),
    Book(
        1,
        "Book 2",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf(),
        true
    ),
    Book(
        2,
        "Book 3",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf(),
        false
    ),
    Book(
        3,
        "Book 4",
        listOf(Author(0, 0, "Richard")),
        listOf(),
        true,
        1000,
        Formats(null, null, null, null, null, null, null, null),
        listOf(),
        "",
        listOf(),
        true
    ),
)