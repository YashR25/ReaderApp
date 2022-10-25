package com.example.readerapp.repository

import com.example.readerapp.data.Resource
import com.example.readerapp.model.Book
import com.example.readerapp.model.Item
import com.example.readerapp.network.ReaderApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val api: ReaderApi) {

    suspend fun getBooks(searchQuery: String): Resource<List<Item>>{
        return try {
            Resource.Loading(data = "Loading....")
            val itemList = api.getAllBooks(searchQuery).items
            Resource.Success(itemList)

        }catch (e: Exception){
            Resource.Error(message = e.message.toString())
        }
    }

    suspend fun getBookInfo(bookId: String): Resource<Item>{
        val response = try {
            Resource.Loading(data = true)
            api.getBookInfo(bookId)
        }catch (e: Exception){
            return Resource.Error(message = e.message.toString())
        }
        Resource.Loading(data = false)
        return Resource.Success(data = response)
    }

}