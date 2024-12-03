package com.example.movie.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.movie.data.AppDatabase
import com.example.movie.data.genre.Genre
import com.example.movie.data.movie.Movie
import com.example.movie.data.movie.Review
import com.example.movie.data.movie.ViewedMovie
import com.example.movie.data.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SimpleDateFormat", "UnusedMaterial3ScaffoldPaddingParameter",
    "UnusedBoxWithConstraintsScope", "AutoboxingStateCreation"
)
@Composable
fun MovieDetailScreen(navController: NavController, database: AppDatabase, movieId: Long, user: User?) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var isViewed by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewText by remember { mutableStateOf("") }
    var reviewRating by remember { mutableIntStateOf(1) }
    var userReviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(movie?.title ?: "") }
    var editAltTitle by remember { mutableStateOf(movie?.alternativeTitle ?: "") }
    var editYear by remember { mutableIntStateOf(movie?.releaseYear ?: 0) }
    var editLength by remember { mutableIntStateOf(movie?.length ?: 0) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isImageEnlarged by remember { mutableStateOf(false) }
    val reviewsList = mutableListOf<Review>()

    LaunchedEffect(movieId) {
        if (movie == null) {
            try {
                movie = database.movieDao().getMovieById(movieId)
                genres = database.movieDao().getGenresForMovie(movieId.toInt())
                if (user != null) {
                    // Проверка, был ли фильм уже просмотрен
                    val count = database.viewedMovieDao().countViewedMoviesByUserIdAndMovieId(user.id, movieId)
                    isViewed = count > 0 // Если количество больше 0, значит фильм просмотрен
                    // Проверяем, загружены ли уже отзывы, и если нет, то загружаем их
                    if (userReviews.isEmpty()) {
                        userReviews = database.reviewDao().getReviewsByUserIdAndMovieId(user.id, movieId)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка загрузки фильма: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }


    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { showEditDialog = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Редактировать")
        }
    }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            item {
                // Логика загрузки и вывода сообщения об ошибке
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                } else if (movie != null) {
                    Column {
                        // Основной контент фильма: кнопка назад, постер, описание и т.д.
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = movie!!.title, style = MaterialTheme.typography.headlineLarge)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (!movie!!.alternativeTitle.isNullOrEmpty()) {
                                    Text(text = movie!!.alternativeTitle!!, style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "${movie!!.releaseYear} • ${movie!!.length} мин", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Жанры: ${genres.joinToString { it.name }}", style = MaterialTheme.typography.bodyLarge)
                            }

                            movie!!.poster?.let {
                                Image(
                                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap(),
                                    contentDescription = "Постер фильма",
                                    modifier = Modifier
                                        .size(192.dp, 288.dp)
                                        .padding(start = 16.dp)
                                        .clickable {
                                            isImageEnlarged = true
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Описание фильма с возможностью раскрытия
                        Text(
                            text = "Описание:\n" + if (isDescriptionExpanded) movie!!.description else movie!!.description.take(40) + "...",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (movie!!.description.length > 40) {
                            TextButton(onClick = { isDescriptionExpanded = !isDescriptionExpanded }) {
                                Text(text = if (isDescriptionExpanded) "Скрыть описание" else "Показать полное описание")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Кнопки для пометки как просмотренный и написания отзыва
                        Row(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = {
                                    if (user != null && movie != null) {
                                        if (isViewed) {
                                            showDialog = true // Показать диалог для удаления
                                        } else {
                                            // Добавление фильма в список просмотренных
                                            val viewedMovie = ViewedMovie(userId = user.id, movieId = movieId, viewedAt = System.currentTimeMillis())
                                            CoroutineScope(Dispatchers.IO).launch {
                                                database.viewedMovieDao().insertViewedMovie(viewedMovie)
                                                isViewed = true // Обновляем состояние
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isViewed) Color.Red else Color.Gray // Меняем цвет на красный, если фильм просмотрен
                                ),
                                modifier = Modifier.weight(1f) // Задаем равную ширину
                            ) {
                                Text(text = if (isViewed) "Просмотрен" else "Не просмотрен")
                            }

                            Spacer(modifier = Modifier.size(16.dp)) // Отступ между кнопками

                            TextButton(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray
                                ),

                                ) {
                                Text(text = "Написать отзыв")
                            }
                        }

                        // Диалог для подтверждения удаления
                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                title = { Text("Подтверждение") },
                                text = { Text("Вы уверены, что хотите удалить фильм из списка просмотренных?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            // Удаляем фильм из списка просмотренных
                                            if (user != null) {
                                                database.viewedMovieDao().deleteViewedMovieByUserIdAndMovieId(user.id, movieId)
                                            }
                                            isViewed = false // Обновляем состояние
                                            showDialog = false // Закрываем диалог
                                        }
                                    }) {
                                        Text("Да")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDialog = false }) {
                                        Text("Нет")
                                    }
                                }
                            )
                        }

                        if (showReviewDialog) {
                            AlertDialog(
                                onDismissRequest = { showReviewDialog = false },
                                title = { Text("Написать отзыв") },
                                text = {
                                    Column {
                                        TextField(
                                            value = reviewText,
                                            onValueChange = { reviewText = it },
                                            label = { Text("Текст отзыва") }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Оценка:")
                                        Row {
                                            (1..5).forEach { rating ->
                                                TextButton(onClick = { reviewRating = rating }) {
                                                    Text(text = rating.toString(), color = if (rating == reviewRating) Color.Blue else Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        if (user != null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                if (!isViewed) {
                                                    // Пометить фильм как просмотренный
                                                    val viewedMovie = ViewedMovie(userId = user.id, movieId = movieId, viewedAt = System.currentTimeMillis())
                                                    database.viewedMovieDao().insertViewedMovie(viewedMovie)
                                                    isViewed = true
                                                }
                                                // Сохранить отзыв
                                                val review = Review(movieId = movieId, userId = user.id, reviewText = reviewText, rating = reviewRating, reviewDate = System.currentTimeMillis())
                                                database.reviewDao().insertReview(review)
                                                showReviewDialog = false
                                            }
                                        }
                                    }) {
                                        Text("Сохранить")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showReviewDialog = false }) {
                                        Text("Отмена")
                                    }
                                }
                            )
                        }
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            ),
                        ) {
                            Text(text = "Удалить фильм")
                        }
                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                title = { Text("Подтверждение удаления") },
                                text = { Text("Вы уверены, что хотите удалить фильм? Это удалит все связанные отзывы и запись о просмотре.") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            // Выполнение фоновой операции, например, удаление данных
                                            database.reviewDao().deleteReviewsByMovieId(movieId)
                                            database.viewedMovieDao().deleteViewedMovieByMovieId(movieId)
                                            movie?.let { database.movieDao().deleteMovie(it) }

                                            // Переключаемся на главный поток, чтобы выполнить навигацию
                                            withContext(Dispatchers.Main) {
                                                // Возвращаем на предыдущую страницу
                                                navController.popBackStack()
                                            }
                                        }

                                    }) {
                                        Text("Да")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Нет")
                                    }
                                }
                            )
                        }
                        if (showEditDialog) {
                            AlertDialog(
                                onDismissRequest = { showEditDialog = false },
                                title = { Text("Редактировать фильм") },
                                text = {
                                    Column {
                                        TextField(
                                            value = editTitle,
                                            onValueChange = { editTitle = it },
                                            label = { Text("Название") }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = editAltTitle,
                                            onValueChange = { editAltTitle = it },
                                            label = { Text("Альтернативное название") }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = editYear.toString(),
                                            onValueChange = { editYear = it.toIntOrNull() ?: editYear },
                                            label = { Text("Год выпуска") }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        TextField(
                                            value = editLength.toString(),
                                            onValueChange = { editLength = it.toIntOrNull() ?: editLength },
                                            label = { Text("Продолжительность (мин)") }
                                        )
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            movie?.let {
                                                it.title = editTitle
                                                it.alternativeTitle = editAltTitle
                                                it.releaseYear = editYear
                                                it.length = editLength
                                                database.movieDao().insertMovie(it) // Сохранение изменений в базе
                                            }
                                            showEditDialog = false
                                        }
                                    }) {
                                        Text("Сохранить")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showEditDialog = false }) {
                                        Text("Отмена")
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Контейнер для отображения отзывов
                        this@LazyColumn.items(userReviews) { review ->
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Дата отзыва: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(review.reviewDate)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(text = "Оценка: ${review.rating}/5", style = MaterialTheme.typography.bodyMedium)
                                review.reviewText?.let {
                                    Text(text = it, style = MaterialTheme.typography.bodyLarge)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider() // Разделитель между отзывами
                            }
                        }
                    }
                }
                }
            }
            if (isImageEnlarged) {
                AlertDialog(
                    onDismissRequest = { isImageEnlarged = false },
                    buttons = {},
                    title = null,
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth() // Заполняет всю ширину экрана
                                .aspectRatio(2f / 3f) // Настраиваем аспект для заполнения без белых полос
                        ) {
                            movie!!.poster?.let {
                                Image(
                                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size).asImageBitmap(),
                                    contentDescription = "Увеличенный постер",
                                    modifier = Modifier
                                        .fillMaxSize() // Постер заполняет всё доступное место
                                        .clip(RoundedCornerShape(8.dp)) // Круглые углы, если нужно
                                )
                            }
                        }
                    }
                )
            }


        }
    }
