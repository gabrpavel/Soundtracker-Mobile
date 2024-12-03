package com.example.movie.data.movie

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews WHERE userId = :userId AND movieId = :movieId")
    suspend fun getReviewByUserAndMovie(userId: Int, movieId: Long): Review?

    @Query("SELECT * FROM reviews WHERE userId = :userId AND movieId = :movieId")
    suspend fun getReviewsByUserIdAndMovieId(userId: Int, movieId: Long): List<Review>

    @Delete
    suspend fun deleteReview(review: Review)

    @Query("DELETE FROM reviews WHERE movieId = :movieId")
    suspend fun deleteReviewsByMovieId(movieId: Long)
}
