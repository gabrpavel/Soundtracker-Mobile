package com.example.movie.data.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.movie.data.movie.Movie

@Dao
interface GenreDao {

    @Insert
    suspend fun insertGenre(genre: Genre): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<Genre>)

    @Query("SELECT * FROM genres")
    suspend fun getAllGenres(): List<Genre>

    @Query("SELECT * FROM genres WHERE name = :name LIMIT 1")
    suspend fun getGenreByName(name: String): Genre?

    @Transaction
    @Query("SELECT * FROM movies WHERE id IN (SELECT movieId FROM MovieGenreCrossRef WHERE genreId = :genreId)")
    suspend fun getMoviesForGenre(genreId: Int): List<Movie>
}