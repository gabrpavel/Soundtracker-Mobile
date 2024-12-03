package com.example.movie.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.movie.data.AppDatabase
import com.example.movie.data.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(navController: NavController, database: AppDatabase, coroutineScope: CoroutineScope) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Почта (email)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        val usernameRegex = "^[A-Za-zА-Яа-я_]{6,}$".toRegex()

        Button(
            onClick = {
                // Проверка на пустые поля
                when {
                    username.isBlank() -> {
                        errorMessage = "Логин не может быть пустым"
                    }
                    !usernameRegex.matches(username) -> {
                        errorMessage = "Логин должен быть длиннее 5 символов и содержать только буквы и символ _"
                    }
                    password.isBlank() -> {
                        errorMessage = "Пароль не может быть пустым"
                    }
                    password.length < 6 -> {
                        errorMessage = "Пароль должен быть длиннее 5 символов"
                    }
                    else -> {
                        coroutineScope.launch {
                            val userExists = database.userDao().getUserByUsername(username)
                            if (userExists == null) {
                                val newUser = User(username = username, password = password, email = email)
                                database.userDao().insertUser(newUser)
                                navController.navigate("movie_screen")
                            } else {
                                errorMessage = "Логин занят. Попробуйте другой"
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }


        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("login_screen") }) {
            Text("Вернуться ко входу")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    // Preview the registration screen
    RegistrationScreen(navController = rememberNavController(), database = AppDatabase.getDatabase(LocalContext.current), coroutineScope = rememberCoroutineScope())
}
