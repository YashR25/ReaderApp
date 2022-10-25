package com.example.readerapp.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class MBook(
    @Exclude
    var id: String? = null,
    var title: String? = null,
    var author: String? = null,
    var notes: String? = null,
    @get:PropertyName("book_photo_url")
    @set:PropertyName("book_photo_url")
    var photoUrl: String? = null,
    @get:PropertyName("book_category")
    @set:PropertyName("book_category")
    var categories: String? = null,
    @get:PropertyName("book_published_date")
    @set:PropertyName("book_published_date")
    var publishDate: String? = null,
    @get:PropertyName("book_rating")
    @set:PropertyName("book_rating")
    var rating: Double? = null,
    @get:PropertyName("book_description")
    @set:PropertyName("book_description")
    var description: String? = null,
    @get:PropertyName("book_page_count")
    @set:PropertyName("book_page_count")
    var pageCount: String? = null,
    @get:PropertyName("started_reading_at")
    @set:PropertyName("started_reading_at")
    var startedReading: Timestamp? = null,
    @get:PropertyName("finished_reading_at")
    @set:PropertyName("finished_reading_at")
    var finishedReading: Timestamp? = null,
    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
    var userId: String? = null,
    @get:PropertyName("google_book_id")
    @set:PropertyName("google_book_id")
    var googleBookId: String? = null,
) {
}