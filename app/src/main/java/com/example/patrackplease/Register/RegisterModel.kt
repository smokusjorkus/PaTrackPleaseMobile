package com.example.patrackplease.Register

import LoginRequest
import android.util.Patterns
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.Register.RegisterRequest.RegisterRequest
import kotlinx.coroutines.*

class RegisterModel : RegisterContract.Model {
    override fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        password: String,
        confirmPass: String,
        callback: RegisterContract.Model.OnRegisterFinishedListener
    ) {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$".toRegex()

        when {
            firstName.isBlank() -> callback.onFirstNameError("First name is required")
            lastName.isBlank() -> callback.onLastNameError("Last name is required")
            username.isBlank() -> callback.onUsernameError("Username is required") // <-- ADDED THIS VALIDATION
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> callback.onEmailError("Enter a valid email")
            password.length < 6 -> callback.onPasswordError("Password must be at least 6 characters")
            !password.contains(passwordRegex) -> callback.onPasswordError("Need 1 uppercase and 1 special character")
            password != confirmPass -> callback.onConfirmPasswordError("Passwords do not match")

            else -> {
                // <-- UPDATED THIS TO INCLUDE ALL FIELDS
                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    confirmPassword = confirmPass, // Now the backend won't complain!
                    username = username            // Now the backend gets the username!
                )

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = ApiClient.apiService.register(request)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body() != null) {
                                val registerResponse = response.body()!!
                                if (registerResponse.resolvedToken() != null && registerResponse.resolvedEmail() != null) {
                                    callback.onSuccess(registerResponse)
                                    return@withContext
                                }

                                launchAutoLogin(email, password, callback)
                            } else {
                                val errorMessage = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                                callback.onFailure(errorMessage ?: "Registration failed: User may already exist")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            callback.onFailure("Server error: ${e.message}")
                        }
                    }
                }
            }
        }
    }

    private fun launchAutoLogin(
        email: String,
        password: String,
        callback: RegisterContract.Model.OnRegisterFinishedListener
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginResponse = ApiClient.apiService.login(LoginRequest(email, password))
                withContext(Dispatchers.Main) {
                    if (loginResponse.isSuccessful && loginResponse.body() != null) {
                        callback.onSuccess(loginResponse.body()!!)
                    } else {
                        callback.onFailure("Registration successful. Please log in with your new account.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onFailure("Registration successful, but auto-login failed: ${e.message}")
                }
            }
        }
    }
}
