package com.example.movie.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movie.data.AppDatabase
import com.example.movie.data.movie.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter", "MutableCollectionMutableState")
@Composable
fun SearchScreen(
    navController: NavController,
    database: AppDatabase,
    coroutineScope: CoroutineScope
) {
    var title by remember { mutableStateOf("") }
    val selectedGenres = remember { mutableStateOf(mutableSetOf<String>()) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var releaseYear by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Movie>>(emptyList()) }

    val movieTypes = listOf("Фильм", "Сериал", "Мультфильм", "Аниме", "Мультсериал", "Телешоу")
    val availableGenres = listOf(
        "аниме", "биографический", "боевик", "вестерн", "военный", "детектив", "детский",
        "документальный", "драма", "исторический", "кинокомикс", "комедия", "концерт",
        "короткометражный", "криминал", "мелодрама", "мистика", "музыка", "мультфильм",
        "мюзикл", "научный", "нуар", "приключения", "реалити-шоу", "семейный", "спорт",
        "ток-шоу", "триллер", "ужасы", "фантастика", "фэнтези", "эротика"
    )

    var typeDropdownExpanded by remember { mutableStateOf(false) }
    var genresDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Поиск фильмов") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Поле для выбора типа фильма с кнопкой очистки внутри поля
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
                ) {
                    TextField(
                        value = selectedType ?: "Выберите тип",
                        onValueChange = {},
                        label = { Text("Тип") },
                        readOnly = true,
                        trailingIcon = {
                            if (selectedType != null) {
                                IconButton(onClick = { selectedType = null }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Очистить тип")
                                }
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth() // Поле занимает всю ширину экрана
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        movieTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedType = type
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

// Поле для выбора жанров с динамическим обновлением выбранных жанров
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = genresDropdownExpanded,
                    onExpandedChange = { genresDropdownExpanded = !genresDropdownExpanded }
                ) {
                    TextField(
                        value = if (selectedGenres.value.isNotEmpty()) selectedGenres.value.joinToString(", ") else "Выберите жанры",
                        onValueChange = {},
                        label = { Text("Жанры") },
                        readOnly = true,
                        trailingIcon = {
                            if (selectedGenres.value.isNotEmpty()) {
                                IconButton(onClick = {
                                    selectedGenres.value.clear()
                                }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Очистить жанры")
                                }
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth() // Поле занимает всю ширину экрана
                    )
                    ExposedDropdownMenu(
                        expanded = genresDropdownExpanded,
                        onDismissRequest = { genresDropdownExpanded = false }
                    ) {
                        availableGenres.forEach { genre ->
                            // Проверка, выбран ли жанр
                            val isSelected = selectedGenres.value.contains(genre)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(genre, modifier = Modifier.weight(1f))
                                        if (isSelected) {
                                            // Добавление галочки для выбранного жанра
                                            Icon(Icons.Default.Check, contentDescription = "Выбрано", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                },
                                onClick = {
                                    // Изменение состояния выбранных жанров
                                    if (isSelected) {
                                        selectedGenres.value.remove(genre)
                                    } else {
                                        selectedGenres.value.add(genre)
                                    }
                                    // Закрываем меню после выбора
                                    genresDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))

            // Поля для года выпуска и продолжительности
            TextField(
                value = releaseYear,
                onValueChange = { releaseYear = it },
                label = { Text("Год выпуска") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = length,
                onValueChange = { length = it },
                label = { Text("Продолжительность (мин)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка для поиска
            Button(
                onClick = {
                    coroutineScope.launch {
                        val genreIds = selectedGenres.value.map { genre ->
                            availableGenres.indexOf(genre) + 1
                        }.filter { it > 0 }

                        searchResults = database.movieDao().searchMovies(
                            title.takeIf { it.isNotEmpty() },
                            releaseYear.toIntOrNull(),
                            selectedType,
                            length.toIntOrNull(),
                            if (genreIds.isNotEmpty()) genreIds else null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Поиск")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение результатов поиска
            if (searchResults.isNotEmpty()) {
                Text("Результаты поиска:", style = MaterialTheme.typography.bodyLarge)

                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searchResults.size) { index ->
                        MovieListItem(searchResults[index], navController)
                    }
                }
            } else {
                Text("Ничего не найдено")
            }
        }
    }
}



@Composable
fun MovieListItem(movie: Movie, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("movie_detail_screen/${movie.id}") },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val posterBitmap = movie.poster?.decodeToBitmap()?.asImageBitmap()

        if (posterBitmap != null) {
            Image(
                bitmap = posterBitmap,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(movie.title, style = MaterialTheme.typography.bodyLarge)
            Text("(${movie.releaseYear})", style = MaterialTheme.typography.bodySmall)
        }
    }
}
