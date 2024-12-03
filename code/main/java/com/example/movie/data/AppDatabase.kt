package com.example.movie.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.movie.data.genre.Genre
import com.example.movie.data.genre.GenreDao
import com.example.movie.data.genre.MovieGenreCrossRef
import com.example.movie.data.movie.Movie
import com.example.movie.data.movie.MovieDao
import com.example.movie.data.movie.Review
import com.example.movie.data.movie.ReviewDao
import com.example.movie.data.movie.ViewedMovie
import com.example.movie.data.movie.ViewedMovieDao
import com.example.movie.data.screenshot.Screenshot
import com.example.movie.data.screenshot.ScreenshotDao
import com.example.movie.data.user.User
import com.example.movie.data.user.UserDao

@Database(
    entities = [User::class, Movie::class, Genre::class, MovieGenreCrossRef::class, Screenshot::class, ViewedMovie::class, Review::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun movieDao(): MovieDao
    abstract fun genreDao(): GenreDao
    abstract fun screenshotDao(): ScreenshotDao
    abstract fun viewedMovieDao(): ViewedMovieDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_2_3) // Подключаем миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Миграция из версии 2 в версию 3
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reviews (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId INTEGER NOT NULL,
                movieId INTEGER NOT NULL,
                reviewText TEXT,
                rating INTEGER NOT NULL,
                reviewDate INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}
