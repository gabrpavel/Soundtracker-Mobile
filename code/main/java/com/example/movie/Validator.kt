package com.example.movie

object Validator {
    init {
        System.loadLibrary("validation")
    }

    external fun validateUsername(username: String): Boolean
    external fun validatePassword(password: String): Boolean
}
