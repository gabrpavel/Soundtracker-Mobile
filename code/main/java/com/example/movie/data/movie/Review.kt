package com.example.movie.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val movieId: Long,
    val reviewText: String?,
    val rating: Int,
    val reviewDate: Long
)
