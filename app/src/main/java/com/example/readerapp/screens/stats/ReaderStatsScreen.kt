package com.example.readerapp.screens.stats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.model.Item
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.example.readerapp.screens.search.BookRow
import com.example.readerapp.utils.formatDate
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Composable
fun ReaderStatsScreen(navController: NavController,viewModel: HomeScreenViewModel) {

    var books: List<MBook> = emptyList()
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(title = "Stats", navController = navController,
        isShowProfile = false, icon = Icons.Default.ArrowBack){
            navController.navigate(ReaderScreens.HomeScreen.name)
        }
    }) {
        androidx.compose.material.Surface() {
            books = if(!viewModel.data.value.data.isNullOrEmpty()){
                viewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            }else{
                emptyList()
            }
            Column {
                Row {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)){
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }
                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0]}".uppercase(Locale.getDefault()))
                }
                Card(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                shape = CircleShape,
                elevation = 5.dp) {

                    val readBooksList = if (!books.isNullOrEmpty()){
                        books.filter { mBook->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }
                    }else{
                        emptyList()
                    }

                    val readingBooks = books.filter {mBook ->
                        (mBook.startedReading != null) && (mBook.finishedReading == null)
                    }
                    
                    Column(modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                    horizontalAlignment = Start) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")
                    }
                }
                if (viewModel.data.value.loading == true){
                    LinearProgressIndicator()
                }else{
                    Divider()
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentPadding = PaddingValues(16.dp)){
                        val readBooks = if (!viewModel.data.value.data.isNullOrEmpty()){
                            viewModel.data.value.data!!.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        }else{
                            emptyList()
                        }
                        items(readBooks){book->
                            BookRowStats(book = book) {
                                navController.navigate(ReaderScreens.DetailScreen.name + "/${book.googleBookId}")
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun BookRowStats(book: MBook,
            onBookClick: () -> Unit){
    androidx.compose.material.Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .clickable {
            onBookClick.invoke()
        },
        color = MaterialTheme.colors.background,
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = book.photoUrl?.ifEmpty {
                "http://books.google.com/books/content?id=ex-tDwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
            }
            AsyncImage(model = imageUrl,
                contentDescription = "Image",
                modifier = Modifier.fillMaxHeight(), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(5.dp))
            Column(modifier = Modifier.padding(1.dp)) {
                Row(horizontalArrangement = SpaceBetween) {
                    Text(text = book.title.toString(),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (book.rating!! >= 4){
                        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                        Icon(imageVector = Icons.Default.ThumbUp, contentDescription = "Thumbs up",
                        tint = Color.Green.copy(0.5f)
                        )
                    }else{
                        Box(){}
                    }
                }

                Text(text = "Author: " + book.author,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
                Text(text = "Started: " + book.startedReading,
                    softWrap = true,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
                Text(text = "Finished: " + formatDate(book.finishedReading!!),
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1)
            }
        }
    }
}