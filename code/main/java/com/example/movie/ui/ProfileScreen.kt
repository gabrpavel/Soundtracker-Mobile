package com.example.movie.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.movie.data.AppDatabase
import com.example.movie.data.movie.Movie
import com.example.movie.data.movie.ViewedMovie
import com.example.movie.data.user.User

@Composable
fun ProfileScreen(navController: NavController, database: AppDatabase, user: User?) {
    if (user != null) {
        var viewedMovies by remember { mutableStateOf<List<ViewedMovie>>(emptyList()) }
        var moviesDetails by remember { mutableStateOf<List<Movie?>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }

        LaunchedEffect(user.id) {
            // Получаем просмотренные фильмы и сортируем их по дате просмотра в порядке убывания
            viewedMovies = database.viewedMovieDao().getViewedMoviesByUserId(user.id).sortedByDescending { it.viewedAt }
            moviesDetails = viewedMovies.take(4).map { viewedMovie ->
                database.movieDao().getMovieById(viewedMovie.movieId)
            }
            isLoading = false
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.username, style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение последних просмотренных фильмов
            Text(text = "Последние просмотренные фильмы:", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    moviesDetails.take(4).forEach { movie ->
                        movie?.let {
                            // Отображение постера фильма
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(3 / 4f)
                                    .clickable {
                                        navController.navigate("movie_detail_screen/${it.id}")
                                    }
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = rememberImagePainter(it.poster),
                                    contentDescription = it.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка для просмотра всех просмотренных фильмов
                Button(onClick = { navController.navigate("viewed_movies_screen") }) {
                    Text(text = "Просмотренные фильмы")
                }
            }
        }
    } else {
        Text("Пользователь не найден", style = MaterialTheme.typography.bodyLarge)
    }
}
