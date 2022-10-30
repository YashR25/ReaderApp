package com.example.readerapp.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readerapp.components.ListCard
import com.example.readerapp.components.ReaderAppBar
import com.example.readerapp.components.RoundedFloatingButton
import com.example.readerapp.components.TitleSection
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

//@Preview
@Composable
fun ReaderHomeScreen(navController: NavController = NavController(LocalContext.current),viewModel: HomeScreenViewModel) {
    Scaffold(topBar = {
                      ReaderAppBar(title = "Reader",navController = navController)
    }, floatingActionButton = {
        RoundedFloatingButton{
            navController.navigate(ReaderScreens.SearchScreen.name)
        }
    }) {
        HomeScreen(navController,viewModel)

    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel){


    var listOfBooks = emptyList<MBook>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    if (!viewModel.data.value.data.isNullOrEmpty()){
        listOfBooks = viewModel.data.value.data!!.filter {mBook ->
            mBook.userId == currentUser?.uid
        }
    }
    val email = if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
    }else{
        "N/A"
    }
    Column(Modifier.padding(2.dp),
    verticalArrangement = Arrangement.Top) {
        Row(modifier = Modifier.align(alignment = Alignment.Start)) {
            TitleSection(label = "Your Reading\n" + "Activity right now...",
            modifier = Modifier)
            Spacer(modifier = Modifier.fillMaxWidth(0.7f))
            Column {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Account",
                modifier = Modifier
                    .clickable {
                        navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                    }
                    .size(45.dp),
                tint = MaterialTheme.colors.secondaryVariant)
                Text(text = email!!,
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.overline,
                color = Color.Red,
                maxLines = 1,
                fontSize = 15.sp,
                overflow = TextOverflow.Clip)

            }
        }
        Divider()
//        ListCard()
        ReadingRightNowArea(listOfBooks,navController = navController)
        
        TitleSection(label = "Reading List", modifier = Modifier)
        BookListArea(list = listOfBooks,navController = navController)
    }
}

@Composable
fun BookListArea(list: List<MBook>,navController: NavController){
    HorizontalScrollableComponent(list){

    }

}

@Composable
fun HorizontalScrollableComponent(list: List<MBook>, onCardPressed: (String) -> Unit) {
    val scrollState = rememberScrollState()
    Row(modifier = Modifier
        .fillMaxWidth()
        .heightIn(250.dp)
        .horizontalScroll(scrollState)) {
        list.forEach {book->
            ListCard(book){title->
                onCardPressed(title)
            }
        }
    }
}

@Composable
fun ReadingRightNowArea(books: List<MBook>, navController: NavController) {
    HorizontalScrollableComponent(books){

    }
    
}





