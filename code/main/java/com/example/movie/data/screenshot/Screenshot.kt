package com.example.movie.data.screenshot

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenshots")
data class Screenshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val movieId: Int,
    val image: ByteArray
)