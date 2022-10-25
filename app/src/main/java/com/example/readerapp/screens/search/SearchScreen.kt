package com.example.readerapp.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.readerapp.components.InputField
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.Item
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens


@Composable
fun SearchScreen(navController: NavController = NavController(LocalContext.current),
viewModel: SearchViewModel){
    Scaffold(topBar = {
        ReaderAppBar(title = "Search", navController = navController,
            isShowProfile = false,
        icon = Icons.Default.ArrowBack){
            navController.popBackStack()
        }
    }) {
        androidx.compose.material.Surface() {
            Column {
                SearchForm(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)){
                    viewModel.searchBooks(it)
                }
                if (viewModel.loading){
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                BookList(navController = navController,
                   list = viewModel.list)
            }
        }

    }

}

@Composable
fun BookList(list: List<Item>,navController: NavController){
    LazyColumn(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()){
        items(list){book->
            BookRow(book = book){
                navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
            }
        }
    }

}

@Composable
fun BookRow(book: Item,
onBookClick: () -> Unit){
    androidx.compose.material.Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .clickable {
                   onBookClick.invoke()
        },
        color = MaterialTheme.colors.background,
    elevation = 4.dp,
    shape = RoundedCornerShape(12.dp)) {
            Row(modifier = Modifier.padding(4.dp),
            verticalAlignment = CenterVertically) {
                val imageUrl = book.volumeInfo.imageLinks.thumbnail.ifEmpty {
                    "http://books.google.com/books/content?id=ex-tDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
                }
                AsyncImage(model = imageUrl,
                    contentDescription = "Image",
                modifier = Modifier.fillMaxHeight(), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.width(5.dp))
                Column(modifier = Modifier.padding(1.dp)) {
                    Text(text = book.volumeInfo.title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1)
                    Text(text = "Author: " + book.volumeInfo.authors[0],
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
                    Text(text = "Date: " + book.volumeInfo.publishedDate,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
                    Text(text = book.volumeInfo.categories.toString(),
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
                }
            }
        }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(modifier: Modifier,
isLoading: Boolean = false,
hint: String = "Search",
onSearch: (String) -> Unit){
    val searchQueryState = rememberSaveable() { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(searchQueryState.value) {
        searchQueryState.value.trim().isNotEmpty()
    }

    InputField(modifier = modifier, labelId = hint, valueState = searchQueryState,
        enabled = true,
        onAction = KeyboardActions{
        if (!valid) return@KeyboardActions
        onSearch.invoke(searchQueryState.value.trim())
        searchQueryState.value = ""
        keyboardController?.hide()
    })

}