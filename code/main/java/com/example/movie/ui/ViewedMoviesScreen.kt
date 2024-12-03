package com.example.movie.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.movie.data.AppDatabase
import com.example.movie.data.movie.Movie
import com.example.movie.data.movie.ViewedMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ViewedMoviesScreen(navController: NavController, database: AppDatabase, userId: Int, coroutineScope: CoroutineScope) {
    var viewedMovies by remember { mutableStateOf<List<ViewedMovie>>(emptyList()) }
    var moviesDetails by remember { mutableStateOf<List<Movie?>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        coroutineScope.launch {
            try {
                viewedMovies = database.viewedMovieDao().getViewedMoviesByUserId(userId)
                moviesDetails = viewedMovies.map { viewedMovie ->
                    database.movieDao().getMovieById(viewedMovie.movieId)
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки просмотренных фильмов: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold() { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                errorMessage.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    Column(modifier = Modifier.padding(16.dp)) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            items(moviesDetails.size) { index ->
                                moviesDetails[index]?.let { movie ->
                                    MoviePoster(movie, navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
