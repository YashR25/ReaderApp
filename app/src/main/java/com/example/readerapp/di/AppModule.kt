package com.example.readerapp.di

import com.example.readerapp.network.ReaderApi
import com.example.readerapp.repository.FirebaseRepository
import com.example.readerapp.utils.Constants.BASE_URL
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideFirebaseRepository(): FirebaseRepository {
        return FirebaseRepository(FirebaseFirestore.getInstance().collection("books"))
    }

    @Singleton
    @Provides
    fun getReaderApi(): ReaderApi{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ReaderApi::class.java)
    }

}