package com.example.readerapp.screens.home

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.DataOrException
import com.example.readerapp.model.MBook
import com.example.readerapp.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val repository: FirebaseRepository): ViewModel() {

    val data: MutableState<DataOrException<List<MBook>, Boolean, Exception>> = mutableStateOf(DataOrException(
        listOf(),true,Exception("")
    ))

    init {
        getAllBookFromDatabase()
    }

    private fun getAllBookFromDatabase() {
        viewModelScope.launch {
            data.value.loading = true
            data.value.data = repository.getAllBooksFromDatabase().data
            if (!data.value.data.isNullOrEmpty()) data.value.loading = false
            Log.d("TAG", "getAllBookFromDatabase: " + data.value.data)
        }
    }
}