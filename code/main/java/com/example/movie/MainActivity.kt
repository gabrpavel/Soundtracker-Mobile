package com.example.movie

//import addSampleData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.movie.data.AppDatabase
import com.example.movie.data.user.CurrentUser
import com.example.movie.ui.AddMovieScreen
import com.example.movie.ui.LoginScreen
import com.example.movie.ui.MovieDetailScreen
import com.example.movie.ui.MovieListScreen
import com.example.movie.ui.ProfileScreen
import com.example.movie.ui.RegistrationScreen
import com.example.movie.ui.SearchScreen
import com.example.movie.ui.ViewedMoviesScreen
import com.example.movie.ui.theme.MovieTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(this)

        setContent {
            MovieTheme {
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login_screen") {
                            LoginScreen(navController, database, coroutineScope)
                        }
                        composable("registration_screen") {
                            RegistrationScreen(navController, database, coroutineScope)
                        }
                        composable("movie_screen") {
                            MovieListScreen(navController, database, coroutineScope)
                        }
                        composable("add_movie_screen") {
                            AddMovieScreen(navController, database, coroutineScope)
                        }
                        composable("movie_detail_screen/{movieId}") { backStackEntry ->
                            val movieId = backStackEntry.arguments?.getString("movieId")
                            if (movieId != null) {
                                MovieDetailScreen(navController, database, movieId.toLong(), CurrentUser.user)
                            }
                        }
                        composable("search_screen") {
                            SearchScreen(navController, database, coroutineScope)
                        }
                        composable("profile_screen") {
                            ProfileScreen(navController, database, CurrentUser.user)
                        }
                        composable("viewed_movies_screen") {
                            ViewedMoviesScreen(navController, database, CurrentUser.user!!.id, coroutineScope)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Отображаем BottomNavigation только на экранах, где он нужен
    if (currentRoute !in listOf("login_screen", "registration_screen")) {
        BottomNavigation {
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                label = { Text("Главная") },
                selected = currentRoute == "movie_screen",
                onClick = {
                    navController.navigate("movie_screen") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Поиск") },
                label = { Text("Поиск") },
                selected = currentRoute == "search_screen",
                onClick = {
                    navController.navigate("search_screen") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Профиль") },
                label = { Text("Профиль") },
                selected = currentRoute == "profile_screen",
                onClick = {
                    navController.navigate("profile_screen") {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.headlineMedium)
    }
}
