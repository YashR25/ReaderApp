package com.example.readerapp.screens.update

import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Start
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readerapp.R
import com.example.readerapp.components.InputField
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedButton
import com.example.readerapp.data.DataOrException
import com.example.readerapp.data.Resource
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.example.readerapp.utils.formatDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UpdateScreen(navController: NavController, bookItemId: String?,homeScreenViewModel: HomeScreenViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        ReaderAppBar(title = "Update", navController = navController,isShowProfile = false,
        icon = Icons.Default.ArrowBack){
            navController.popBackStack()
        }
    }) {
        val bookInfo = produceState<DataOrException<List<MBook>,Boolean,Exception>>(initialValue = DataOrException(
            emptyList(),true, Exception("")
        )){
            value = homeScreenViewModel.data.value
        }.value

        androidx.compose.material.Surface(modifier = Modifier
            .fillMaxSize()
            .padding(3.dp)) {
            Column(modifier = Modifier
                .padding(3.dp)
                .padding(3.dp),
            verticalArrangement = Top,
            horizontalAlignment = CenterHorizontally) {
                if (bookInfo.loading == true){
                    LinearProgressIndicator()
                }else{
                    androidx.compose.material.Surface(modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth(),
                        elevation = 4.dp,
                    shape = CircleShape) {
                        ShowBookUpdate(bookInfo,bookItemId!!)
                    }
                   ShowSimpleForm(bookInfo.data?.first{
                       it.googleBookId == bookItemId
                   },navController)
                }

            }

        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowSimpleForm(book: MBook?,navController: NavController) {
    Log.d("TAG", "ShowSimpleForm: $book")
    val notesText = remember {
        mutableStateOf(book?.notes)
    }
    val ratingValue = remember {
        mutableStateOf(book?.rating?.toInt())
    }
    val isStartReading =  remember {
        mutableStateOf(false)
    }
    val isFinishedReading =  remember {
        mutableStateOf(false)
    }

    SimpleForm(defaultValue = book?.notes.toString().ifEmpty { "No thoughts available." }){note->
        notesText.value = note
    }
    Row(modifier = Modifier.padding(4.dp),
    verticalAlignment = CenterVertically,
    horizontalArrangement = Start) {
        TextButton(onClick = { isStartReading.value = true },
        enabled = book?.startedReading == null) {
            if (book?.startedReading == null){
            if (!isStartReading.value){
                Text(text = "Start Reading")
            }else{
                Text(text = "Started Reading!",
                modifier = Modifier.alpha(0.6f),
                color = Color.Red.copy(alpha = 0.5f))
            }

        }else{
            Text(text = "Started Reading: ${formatDate(book.startedReading!!) }")
            }
    }
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = { isFinishedReading.value = true },
        enabled = book?.finishedReading == null) {
            if (book?.finishedReading == null){
                if (!isFinishedReading.value){
                    Text(text = "Mark As Read")
                }else{
                    Text(text = "Finished Reading!")
                }
            }else{
                Text(text = "Finished On: ${formatDate(book.finishedReading!!) }")
            }
        }


        }
    Text(text = "Rating", modifier = Modifier.padding(3.dp))
    book?.rating?.toInt().let {
        Log.d("TAG", "showSimpleForm: $it")
        RatingBar(rating = it!!){rating->
            ratingValue.value = rating
        }
    }
    Spacer(modifier = Modifier.padding(bottom = 15.dp))
    Row {
        val changedNotes = book?.notes != notesText.value
        val changedRating = book?.rating?.toInt() != ratingValue.value
//        Log.d("TAG", "showSimpleForm: " + ratingValue.value)
        val isFinishedTimeStamp = if (isFinishedReading.value) Timestamp.now() else book?.finishedReading
        val isStartedTime = if (isStartReading.value) Timestamp.now() else book?.startedReading

        val bookUpdate = changedNotes || changedRating || isStartReading.value || isFinishedReading.value
        Log.d("TAG", "ShowSimpleForm: ${notesText.value}")


        val bookToUpdate = hashMapOf(
            "finished_reading_at" to isFinishedTimeStamp,
            "started_reading_at" to isStartedTime,
            "book_rating" to ratingValue.value!!.toDouble(),
            "book_notes" to notesText.value
        ).toMap()
        val context = LocalContext.current
        RoundedButton(label = "Update"){
            if(bookUpdate){
                FirebaseFirestore.getInstance()
                    .collection("books")
                    .document(book?.id!!)
                    .update(bookToUpdate)
                    .addOnCompleteListener{
                        showToast(context,"Book Updated Successfully!")
                        navController.navigate(ReaderScreens.HomeScreen.name)

                    }.addOnFailureListener{
                        Log.d("TAG", "showSimpleForm: " + "Something went wrong..")
                    }
            }
        }
        Spacer(modifier = Modifier.width(100.dp))
        val openDialog = remember {
            mutableStateOf(false)
        }

        if (openDialog.value){
            showAlertDialog(title = stringResource(id = R.string.sure) + "\n" + stringResource(id = R.string.action),openDialog){
                FirebaseFirestore.getInstance().collection("books")
                    .document(book?.id!!)
                    .delete()
                    .addOnCompleteListener{
                        if (it.isSuccessful){
                            openDialog.value = false
                            navController.navigate(ReaderScreens.HomeScreen.name)
                        }
                    }
            }
        }
        RoundedButton(label = "Delete"){
            openDialog.value = true
        }
    }
        


}

@Composable
fun showAlertDialog(title: String,
openDialog: MutableState<Boolean>,
onYesPressed: () -> Unit) {
    if(openDialog.value){
        AlertDialog(onDismissRequest = { /*TODO*/ },
        title = {
                Text(text = "Delete Book")
        },
        text = { Text(text = title)},
        buttons = {
            Row(horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = { onYesPressed.invoke() }) {
                    Text(text = "Yes")
                }
                TextButton(onClick = { openDialog.value = false }) {
                    Text(text = "No")
                }
            }
        })
    }


}

fun showToast(context: Context, s: String) {
    Toast.makeText(context,s,Toast.LENGTH_SHORT)

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Great Book!",
    onSearch: (String) -> Unit
){
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyBoardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value) {
            textFieldValue.value.trim().isNotEmpty()
        }
        InputField(modifier = Modifier
            .height(140.dp)
            .fillMaxWidth()
            .padding(3.dp),
            labelId ="Enter Your Thoughts",
            valueState = textFieldValue,
        enabled = true,
        onAction = KeyboardActions{
            if (valid) {
                onSearch.invoke(textFieldValue.value.trim())
                keyBoardController?.hide()
            }
        })
    }
}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>,bookItemId: String) {
    Row() {
        Spacer(modifier = Modifier.width(43.dp))
        if (bookInfo.data != null){
            Column(modifier = Modifier.padding(4.dp),
            verticalArrangement = Center) {
                CardListItem(bookInfo.data!!.first { book->
                    book.googleBookId == bookItemId
                }){

                }

            }
        }
        
    }


}

@Composable
fun CardListItem(book: MBook, onPressDetail: () -> Unit) {
        Card(modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { },
        elevation = 8.dp) {
            Row(horizontalArrangement = Start) {
                AsyncImage(model = book.photoUrl.toString(), contentDescription = "Book Image",
                    modifier = Modifier
                        .height(100.dp)
                        .width(120.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 120.dp,
                                topEnd = 20.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        ))

                Column {
                    Text(text = book.title.toString(),
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .width(120.dp),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis)
                    Text(text = book.publishDate.toString(),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(start = 8.dp,
                            end = 8.dp, top = 0.dp, bottom = 8.dp))

                }
            }

        }


}

@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onPressRating: (Int) -> Unit
) {
    var ratingState by remember {
        mutableStateOf(rating)
    }

    var selected by remember {
        mutableStateOf(false)
    }
    val size by animateDpAsState(
        targetValue = if (selected) 42.dp else 34.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier.width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_star_24),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                onPressRating(i)
                                ratingState = i
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
}
