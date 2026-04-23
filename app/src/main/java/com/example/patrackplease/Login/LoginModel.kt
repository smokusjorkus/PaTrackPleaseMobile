package com.example.patrackplease.Login

import android.util.Patterns

class LoginModel : LoginContract.Model {

    override fun login(
        email: String,
        password: String,
        callback: LoginContract.Model.OnLoginFinishedListener
    ){
        when{
            email.isBlank() -> {
                callback.onEmailError("Email cannot be empty")
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                callback.onEmailError("Enter a valid email")
            }

            password.isBlank() -> {
                callback.onPasswordError("Password cannot be empty")
            }

            password.length < 6 -> {
                callback.onPasswordError("Password must be at least 6 characters")
            }

            email == "admin@gmail.com" && password == "123456" -> {
                callback.onSuccess("Login successful")
            }

            else -> {
                callback.onFailure("Invalid email or password")
            }
        }
    }
}