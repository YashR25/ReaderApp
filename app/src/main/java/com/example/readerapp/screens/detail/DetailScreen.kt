package com.example.readerapp.screens.detail

import android.text.Html
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.SpaceAround
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedButton
import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.model.MBook
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DetailScreen(navController: NavController,bookId: String,
detailViewModel: DetailViewModel = hiltViewModel()) {

    Scaffold(topBar = {
        ReaderAppBar(title = "Book Detail", navController = navController,
        icon = Icons.Default.ArrowBack,
        isShowProfile = false){
            navController.popBackStack()
        }
    }) {
        androidx.compose.material.Surface(modifier = Modifier
            .padding(3.dp)
            .fillMaxSize()) {
            Column(modifier = Modifier.padding(12.dp),
            verticalArrangement = Top,
            horizontalAlignment = CenterHorizontally) {
                val bookInfo = produceState<Resource<Item>>(
                    initialValue = Resource.Loading()){
                    value = detailViewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null){
                    LinearProgressIndicator()
                }else{
                    ShowBookDetails(bookInfo,navController)
                }

            }

        }
    }

}

@Composable
fun ShowBookDetails(bookInfo: Resource<Item>, navController: NavController) {
    val bookData = bookInfo.data?.volumeInfo
    val googleBookId = bookInfo.data?.id
    Card(modifier = Modifier.padding(34.dp),
    shape = CircleShape, elevation = 4.dp) {
        AsyncImage(model = bookData?.imageLinks?.thumbnail, contentDescription = "Book Image",
        modifier = Modifier
            .width(90.dp)
            .heightIn(90.dp)
            .padding(1.dp))
    }
    Text(text = bookData?.title.toString(), style = MaterialTheme.typography.h6, overflow = TextOverflow.Ellipsis,
        fontSize = 19.sp)
    Text(text = "Author: " + bookData?.authors.toString(), style = MaterialTheme.typography.subtitle1)
    Text(text = "Pages: " + bookData?.pageCount.toString(), style = MaterialTheme.typography.subtitle1)
    Text(text = "Categories: " + bookData?.categories.toString(), style = MaterialTheme.typography.subtitle1,
    overflow = TextOverflow.Ellipsis,
    maxLines = 3)
    Text(text = "Published Date: " + bookData?.publishedDate.toString(), style = MaterialTheme.typography.subtitle1)
    Spacer(modifier = Modifier.height(5.dp))

    val cleanDescription = HtmlCompat.fromHtml(bookData?.description!!, HtmlCompat.FROM_HTML_MODE_LEGACY)

    val localDimens = LocalContext.current.resources.displayMetrics
    Surface(modifier = Modifier
        .padding(2.dp)
        .height(localDimens.heightPixels.dp.times(0.09f)),
        shape = RectangleShape,
        border = BorderStroke(2.dp, color = Color.LightGray)
    ) {
        LazyColumn(modifier = Modifier.padding(3.dp)){
            item {
                Text(text = cleanDescription.toString())
            }
        }
    }

    Row(modifier = Modifier.padding(top = 6.dp),
    horizontalArrangement = SpaceAround) {
        RoundedButton(label = "Save"){
            val book = MBook(
                title = bookData.title.toString(),
                author = bookData.authors.toString(),
                description = bookData.description.toString(),
                categories = bookData.categories.toString(),
                notes = "",
                photoUrl = bookData.imageLinks.thumbnail,
                publishDate = bookData.publishedDate,
                pageCount = bookData.pageCount.toString(),
                rating = 0.0,
                googleBookId = googleBookId,
                userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
            )
            saveToFirebase(book,navController)
        }
        Spacer(modifier = Modifier.width(12.dp))
        RoundedButton(label = "Cancel"){
            navController.popBackStack()
        }

    }


}

fun saveToFirebase(book: MBook,navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")

    if (book.toString().isNotEmpty()){
        dbCollection.add(book)
            .addOnSuccessListener {docRef->
                val docId = docRef.id
                dbCollection.document(docId).update(
                    hashMapOf("id" to docId) as Map<String, Any>)
                    .addOnCompleteListener{task->
                        if (task.isSuccessful){
                            navController.popBackStack()
                        }
                    }
                    .addOnFailureListener{
                        Log.d("TAG", "saveToFirebase: " + "Error updating document")
                    }
            }
    }else{

    }

}
