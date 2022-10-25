package com.example.readerapp.screens.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Arrangement.Top
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readerapp.R
import com.example.readerapp.components.AppLogo
import com.example.readerapp.components.EmailInputField
import com.example.readerapp.components.InputField
import com.example.readerapp.navigation.ReaderScreens


@Composable
fun LoginScreen(navController: NavController,
viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val showLoginForm = rememberSaveable{
        mutableStateOf(false)
    }
    androidx.compose.material.Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Top,
        modifier = Modifier.fillMaxWidth()) {
            AppLogo()
            if (showLoginForm.value) {
                UserForm(loading = false, isCreateAccount = false){email,password->
                    viewModel.signInWithEmailAndPassword(email,password){
                        navController.navigate(ReaderScreens.HomeScreen.name)
                    }
                    Log.d("TAG", "LoginScreen: $email & $password")
                }
            } else {
                UserForm(loading = false, isCreateAccount = true){email,password->
                    viewModel.createUserWithEmailAndPassword(email,password){
                        navController.navigate(ReaderScreens.HomeScreen.name)
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.padding(15.dp),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Center) {
                val text = if (showLoginForm.value) "Sign Up" else "Login"
                Text(text = "New User?")
                Text(text, modifier = Modifier
                    .clickable {
                        showLoginForm.value = !showLoginForm.value
                    }
                    .padding(start = 5.dp),
                fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondaryVariant
                )
                
            }
        }

    }
    
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String,String) -> Unit){
    val email = rememberSaveable{ mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val valid = remember(email.value,password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordVisibility = remember {
        mutableStateOf(false)
    }
    val passwordFocusRequester = FocusRequester.Default
    Column(modifier = Modifier
        .height(250.dp)
        .fillMaxWidth()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Center) {
        if(isCreateAccount) Text(text = stringResource(id = R.string.create_acct), color = MaterialTheme.colors.onBackground) else Text(text = "")
        EmailInputField(
            modifier = Modifier.fillMaxWidth(),
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions {
                passwordFocusRequester.requestFocus()
            }
        )
        PasswordInputField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            passwordState = password,
            onAction = KeyboardActions{
                if (!valid) return@KeyboardActions
                onDone(email.value,password.value)
            }
        )
        SubmitButton(
            if (isCreateAccount) "Create Account" else "Login",
            loading = loading,
            validInputs = valid,
        ){
            onDone(email.value.trim(),password.value.trim())
            keyboardController?.hide()
        }
    }


}

@Composable
fun SubmitButton(textId: String,
                 loading: Boolean,
                 validInputs: Boolean,
onClick:() -> Unit) {
    Button(onClick = onClick,
    modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth(),
    enabled = !loading && validInputs,
    shape = CircleShape)
    {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp)) else Text(
            text = textId,
            modifier = Modifier.padding(5.dp)
        )


    }


}

@Composable
fun PasswordInputField(
    modifier: Modifier,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    passwordState: MutableState<String>,
    onAction: KeyboardActions
) {

    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()
    InputField(modifier = modifier,
        labelId = "Password",
        valueState = passwordState,
    onAction = onAction,
    keyboardType = KeyboardType.Password,
    imeAction = ImeAction.Done,
    visualTransformation = visualTransformation,
    trailingIcon = {PasswordVisibility(passwordState = passwordVisibility)})
}

@Composable
fun PasswordVisibility(passwordState: MutableState<Boolean>) {
    val visible = passwordState.value
    IconButton(onClick = { passwordState.value = !visible }) {
        Icons.Default.Close
    }

}



