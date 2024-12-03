package com.example.movie.data.screenshot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScreenshotDao {
    @Insert
    suspend fun insertScreenshot(screenshot: Screenshot)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreenshots(screenshots: List<Screenshot>)

    @Query("SELECT * FROM screenshots WHERE movieId = :movieId")
    suspend fun getScreenshotsForMovie(movieId: Int): List<Screenshot>
}
