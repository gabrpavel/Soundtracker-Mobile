package com.example.movie.data.movie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ViewedMovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertViewedMovie(viewedMovie: ViewedMovie)

    @Query("SELECT * FROM viewed_movies WHERE userId = :userId ORDER BY viewedAt DESC")
    suspend fun getViewedMoviesByUserId(userId: Int): List<ViewedMovie>

    @Query("SELECT COUNT(*) FROM viewed_movies WHERE userId = :userId AND movieId = :movieId")
    suspend fun countViewedMoviesByUserIdAndMovieId(userId: Int, movieId: Long): Int

    @Query("SELECT * FROM viewed_movies WHERE userId = :userId AND movieId = :movieId")
    suspend fun getViewedMovie(userId: Int, movieId: Long): ViewedMovie?

    @Query("DELETE FROM viewed_movies WHERE userId = :userId AND movieId = :movieId")
    suspend fun deleteViewedMovieByUserIdAndMovieId(userId: Int, movieId: Long)

    @Query("DELETE FROM viewed_movies WHERE movieId = :movieId")
    suspend fun deleteViewedMovieByMovieId(movieId: Long)
}
