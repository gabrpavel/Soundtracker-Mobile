package com.example.movie.data.movie

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.movie.data.genre.Genre
import com.example.movie.data.genre.MovieGenreCrossRef

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie): Long

    @Update
    suspend fun updateMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieGenres(crossRef: MovieGenreCrossRef)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Long): Movie?

    @Transaction
    @Query("SELECT * FROM genres WHERE id IN (SELECT genreId FROM MovieGenreCrossRef WHERE movieId = :movieId)")
    suspend fun getGenresForMovie(movieId: Int): List<Genre>

    @Delete
    suspend fun deleteMovie(movie: Movie)

    // Запрос для поиска по названию и фильтрам
    @Transaction
    @Query("""
    SELECT * FROM movies 
    WHERE (:title IS NULL OR title LIKE '%' || :title || '%')
    AND (:releaseYear IS NULL OR releaseYear = :releaseYear)
    AND (:type IS NULL OR type = :type)
    AND (:length IS NULL OR length = :length)
    AND (:genreIds IS NULL OR :genreIds = '' OR id IN (SELECT movieId FROM MovieGenreCrossRef WHERE genreId IN (:genreIds)))
""")
    suspend fun searchMovies(
        title: String?,
        releaseYear: Int?,
        type: String?,
        length: Int?,
        genreIds: List<Int>?
    ): List<Movie>

}