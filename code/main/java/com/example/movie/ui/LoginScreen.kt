package com.example.movie.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.movie.data.AppDatabase
import com.example.movie.data.user.CurrentUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, database: AppDatabase, coroutineScope: CoroutineScope) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Вход", style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text
            )
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
                            val user = database.userDao().getUserByUsername(username)
                            if (user != null && user.password == password) {
                                // Успешный вход, переход на главный экран
                                CurrentUser.user = user
                                navController.navigate("movie_screen")
                            } else {
                                errorMessage = "Неверный логин или пароль"
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }


        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { navController.navigate("registration_screen") }) {
            Text("Регистрация")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    // Preview the login screen
    LoginScreen(navController = rememberNavController(), database = AppDatabase.getDatabase(LocalContext.current), coroutineScope = rememberCoroutineScope())
}
