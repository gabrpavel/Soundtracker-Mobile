//import com.example.movie.data.AppDatabase
//import com.example.movie.data.movie.Movie
//import com.example.movie.data.genre.Genre
//import com.example.movie.data.screenshot.Screenshot
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//fun addSampleData(database: AppDatabase, coroutineScope: CoroutineScope) {
//    coroutineScope.launch {
//        // Создаем несколько записей для фильмов
//        val movies = listOf(
//            Movie(title = "Movie 1", alternativeTitle = "Alt Movie 1", type = "Drama", poster = ByteArray(0), description = "Description of Movie 1"),
//            Movie(title = "Movie 2", alternativeTitle = "Alt Movie 2", type = "Action", poster = ByteArray(0), description = "Description of Movie 2"),
//            Movie(title = "Movie 3", alternativeTitle = "Alt Movie 3", type = "Comedy", poster = ByteArray(0), description = "Description of Movie 3")
//        )
//
//        // Вставляем фильмы в базу данных
//        database.movieDao().insertMovies(movies)
//
//        // Создаем несколько записей для жанров
//        val genres = listOf(
//            Genre(name = "Drama"),
//            Genre(name = "Action"),
//            Genre(name = "Comedy")
//        )
//
//        // Вставляем жанры в базу данных
//        database.genreDao().insertGenres(genres)
//
//        // Создаем несколько записей для скриншотов
//        val screenshots = listOf(
//            Screenshot(movieId = 1, image = ByteArray(0)), // Здесь добавьте ваши данные изображения
//            Screenshot(movieId = 2, image = ByteArray(0)),
//            Screenshot(movieId = 3, image = ByteArray(0))
//        )
//
//        // Вставляем скриншоты в базу данных
//        database.screenshotDao().insertScreenshots(screenshots)
//    }
//}
