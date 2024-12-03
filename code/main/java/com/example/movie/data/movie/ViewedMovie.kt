package com.example.movie.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewed_movies")
data class ViewedMovie(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val movieId: Long,
    val viewedAt: Long // timestamp for when the movie was viewed
)
