package com.example.readerapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readerapp.data.Resource
import com.example.readerapp.model.Item
import com.example.readerapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: BookRepository): ViewModel() {
    var list: List<Item> by mutableStateOf(listOf())
    var loading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("android")
    }

     fun searchBooks(searchQuery: String) {
        viewModelScope.launch {
            if (searchQuery.isEmpty()){
                return@launch
            }
            try {
                when(val result = repository.getBooks(searchQuery)){
                    is Resource.Success -> {
                        loading = false
                        list = result.data!!
                        Log.d("TAG", "searchBooks: " + "called")
                    }
                    is Resource.Error -> {
                        loading = false
                        Log.d("TAG", "searchBooks: " + result.message)

                    }
                    is Resource.Loading -> {
                        loading = true
                    }
                    else -> {
                        loading = false
                    }

                }
                repository.getBooks(searchQuery)
            }catch (e: Exception){
                loading = false
                Log.d("TAG", "searchBooks: " + e.message)

            }

        }
    }

}