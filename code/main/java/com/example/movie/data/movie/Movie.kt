package com.example.movie.data.movie

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var alternativeTitle: String?,
    val type: String,
    val poster: ByteArray?,
    val description: String,
    var releaseYear: Int,
    var length: Int
)
