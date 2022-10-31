package com.example.readerapp.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.readerapp.model.MBook
import com.example.readerapp.navigation.ReaderScreens
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppLogo() {
    Text(
        text = "Reader",
        style = MaterialTheme.typography.h3,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
fun EmailInputField(modifier: Modifier = Modifier,
                    emailState: MutableState<String>,
                    enabled: Boolean = true,
                    imeAction: ImeAction = ImeAction.Next,
                    labelId: String = "Email",
                    onAction: KeyboardActions = KeyboardActions.Default) {
    InputField(
        modifier = modifier,
        labelId = labelId,
        valueState = emailState,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        enabled = enabled,
        onAction = onAction)


}

@Composable
fun InputField(
    modifier: Modifier,
    labelId: String,
    valueState: MutableState<String>,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isSingleLine: Boolean = true,
    maxLine: Int = 1,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable () -> Unit = {},
    onAction: KeyboardActions = KeyboardActions.Default
) {

    OutlinedTextField(value = valueState.value,
        onValueChange = {valueState.value = it},
        modifier = modifier.padding(top = 10.dp, end = 10.dp, start = 10.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        enabled = enabled,
        singleLine = isSingleLine,
        maxLines = maxLine,
        keyboardActions = onAction,
        label = { Text(text = labelId)},
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon
    )

}

@Composable
fun TitleSection(label: String,modifier: Modifier) {
    Text(text = label, textAlign = TextAlign.Left, modifier = modifier.padding(5.dp))
}

@Composable
fun ReaderAppBar(title: String, navController: NavController,
                 isShowProfile:Boolean = true,
icon: ImageVector? = null,
onIconClicked: () -> Unit = {}) {
    TopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start) {
                if (isShowProfile){
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Icon", tint = MaterialTheme.colors.onBackground)
                }
                if (icon != null){
                    Icon(imageVector = icon, contentDescription = "Back Arrow", tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.clickable {
                        onIconClicked.invoke()
                    })
                    Spacer(modifier = Modifier.width(40.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

    }, backgroundColor = Color.Transparent,
        elevation = 0.dp,
        actions = {
            if(isShowProfile){
                Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout Icon",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clickable {
                            FirebaseAuth
                                .getInstance()
                                .signOut()
                                .run {
                                    navController.navigate(ReaderScreens.LoginScreen.name)
                                }
                        })
            }else Box() {}
        })
}

@Composable
fun RoundedFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = { onClick.invoke() },
        backgroundColor = Color(0xFF89C5DD)
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Button", tint = Color.Black)
    }

}

@Preview
@Composable
fun ListCard(book: MBook = MBook("","","", ""), onPressDetail: (String) -> Unit = {} ){

    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics
    val displayWidth = displayMetrics.widthPixels / displayMetrics.density
    val spacing = 10.dp
    Card(shape = RoundedCornerShape(29.dp),
        backgroundColor = MaterialTheme.colors.background,
        elevation = 6.dp,
        modifier = Modifier
            .padding(16.dp)
            .width(202.dp)
            .height(242.dp)
            .clickable {
                onPressDetail.invoke(book.googleBookId.toString())
            }) {
        Column(
            modifier = Modifier.width(displayWidth.dp - (spacing * 2)),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                AsyncImage(model = book.photoUrl, contentDescription = "Book Image",
                    modifier = Modifier
                        .width(100.dp)
                        .height(140.dp)
                        .padding(4.dp))
                Spacer(modifier = Modifier.width(50.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 25.dp)
                ) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Favourite")
                    BookRating(book.rating!!)
                }
            }
            Text(text = book.title.toString(),
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Text(text = "Author: ${book.author}", modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.caption)
            val isStartedReading = remember{
                mutableStateOf(false)
            }
            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
                isStartedReading.value = book.startedReading != null
                RoundedButton(label = if(isStartedReading.value) "Reading" else "Not Yet",
                radius = 70)
            }
        }

    }
}

@Composable
fun BookRating(score: Double = 4.5) {
    Surface(modifier = Modifier
        .height(70.dp)
        .padding(4.dp),
        shape = RoundedCornerShape(56.dp),
        elevation = 6.dp,
        color = MaterialTheme.colors.background) {
        Column(Modifier.padding(4.dp)) {
            Icon(imageVector = Icons.Default.StarBorder, contentDescription = "Star")
            Text(text = score.toString(), color = MaterialTheme.colors.onBackground)
        }


    }
}

@Composable
fun RoundedButton(
    radius: Int = 29,
    label: String = "Reading",
    onPress: () -> Unit = {}
){
    Surface(modifier = Modifier.clip(
        RoundedCornerShape(
        bottomEndPercent = radius,
        topStartPercent = radius,)
    ),
        color = Color(0xFF92CBDF)) {
        Column(modifier = Modifier
            .width(90.dp)
            .heightIn(40.dp)
            .clickable {
                onPress.invoke()
            },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(text = label, color = MaterialTheme.colors.onBackground, fontSize = 15.sp)

        }
    }
}