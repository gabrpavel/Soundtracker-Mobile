package com.example.movie.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movie.data.AppDatabase
import com.example.movie.data.genre.Genre
import com.example.movie.data.genre.GenreDao
import com.example.movie.data.genre.MovieGenreCrossRef
import com.example.movie.data.movie.Movie
import com.example.movie.data.screenshot.Screenshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun AddMovieScreen(navController: NavController, database: AppDatabase, coroutineScope: CoroutineScope) {
    var title by remember { mutableStateOf("") }
    var alternativeTitle by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var releaseYear by remember { mutableStateOf("") }  // Для года выпуска
    var length by remember { mutableStateOf("") }       // Для продолжительности фильма
    val selectedGenres = remember { mutableStateOf(mutableSetOf<String>()) }  // Для выбора нескольких жанров
    var posterUri by remember { mutableStateOf<Uri?>(null) }
    var posterBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    val movieTypes = listOf("Фильм", "Сериал", "Мультфильм", "Аниме", "Мультсериал", "Телешоу")
    val availableGenres = listOf(
        "аниме", "биографический", "боевик", "вестерн", "военный", "детектив", "детский",
        "документальный", "драма", "исторический", "кинокомикс", "комедия", "концерт",
        "короткометражный", "криминал", "мелодрама", "мистика", "музыка", "мультфильм",
        "мюзикл", "научный", "нуар", "приключения", "реалити-шоу", "семейный", "спорт",
        "ток-шоу", "триллер", "ужасы", "фантастика", "фэнтези", "эротика"
    )

    var expanded by remember { mutableStateOf(false) }
    var expandedGenre by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        posterUri = uri
        posterUri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            posterBitmap = BitmapFactory.decodeStream(inputStream)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Кнопка "Назад"
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = alternativeTitle,
                    onValueChange = { alternativeTitle = it },
                    label = { Text("Альтернативное название") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = type,
                        onValueChange = {},
                        label = { Text("Тип") },
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        movieTypes.forEach { selectedType ->
                            DropdownMenuItem(
                                text = { Text(selectedType) },
                                onClick = {
                                    type = selectedType
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = releaseYear,
                    onValueChange = { releaseYear = it },
                    label = { Text("Год выпуска") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = length,
                    onValueChange = { length = it },
                    label = { Text("Продолжительность (мин)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = expandedGenre,
                    onExpandedChange = { expandedGenre = !expandedGenre }
                ) {
                    TextField(
                        value = if (selectedGenres.value.isNotEmpty()) selectedGenres.value.joinToString() else "Выберите жанры",
                        onValueChange = {},
                        label = { Text("Жанры") },
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedGenre,
                        onDismissRequest = { expandedGenre = false }
                    ) {
                        availableGenres.forEach { genre ->
                            DropdownMenuItem(
                                text = { Text(genre) },
                                onClick = {
                                    if (selectedGenres.value.contains(genre)) {
                                        selectedGenres.value.remove(genre)
                                    } else {
                                        selectedGenres.value.add(genre)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            item {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Выбрать постер")
                }
            }

            item {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (title.isNotEmpty() && type.isNotEmpty() && description.isNotEmpty() && selectedGenres.value.isNotEmpty() && releaseYear.isNotEmpty() && length.isNotEmpty()) {
                                try {
                                    val posterByteArray = posterBitmap?.let { resizeAndConvertToByteArray(it, 300, 450) }
                                    val movie = Movie(
                                        title = title,
                                        alternativeTitle = alternativeTitle,
                                        type = type,
                                        poster = posterByteArray,
                                        description = description,
                                        releaseYear = releaseYear.toInt(),
                                        length = length.toInt()
                                    )

                                    val movieId = database.movieDao().insertMovie(movie)

                                    selectedGenres.value.forEach { genreName ->
                                        val existingGenre = database.genreDao().getGenreByName(genreName)
                                        val genreId: Int

                                        if (existingGenre == null) {
                                            val newGenre = Genre(name = genreName)
                                            genreId = database.genreDao().insertGenre(newGenre).toInt()
                                        } else {
                                            genreId = existingGenre.id
                                        }

                                        database.movieDao().insertMovieGenres(
                                            MovieGenreCrossRef(movieId = movieId.toInt(), genreId = genreId)
                                        )
                                    }
                                    navController.navigate("movie_screen")
                                } catch (e: Exception) {
                                    errorMessage = "Ошибка добавления фильма: ${e.message}"
                                }
                            } else {
                                errorMessage = "Все поля должны быть заполнены, и должен быть выбран хотя бы один жанр"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Добавить фильм")
                }
            }

            item {
                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

fun resizeAndConvertToByteArray(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): ByteArray {
    val resizedBitmap = bitmap.resize(maxWidth, maxHeight)
    return bitmapToByteArray(resizedBitmap)
}

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
    return Bitmap.createScaledBitmap(this, (width * ratio).toInt(), (height * ratio).toInt(), true)
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}
