package com.example.patrackplease.Login

import LoginRequest
import android.util.Patterns
import com.example.patrackplease.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginModel : LoginContract.Model {

    override fun login(
        email: String,
        password: String,
        callback: LoginContract.Model.OnLoginFinishedListener
    ) {
        // Regex: At least one uppercase letter and one special character
        val passwordRegex = "^(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$".toRegex()

        when {
            email.isBlank() -> callback.onEmailError("Email cannot be empty")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> callback.onEmailError("Enter a valid email")
            password.isBlank() -> callback.onPasswordError("Password cannot be empty")
            password.length < 6 -> callback.onPasswordError("Password must be at least 6 characters")
            !password.contains(passwordRegex) -> callback.onPasswordError("Password must contain uppercase and special character")

            else -> {
                // Prepare the data to send to the API
                val request = LoginRequest(email, password)

                // Perform network operation on IO thread
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = ApiClient.apiService.login(request)

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful && response.body() != null) {
                                // FIX: Pass the whole body (LoginResponse) back to the Presenter
                                callback.onSuccess(response.body()!!)
                            } else {
                                callback.onFailure("Invalid email or password")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            callback.onFailure("Network error: ${e.message}")
                        }
                    }
                }
            }
        }
    }
}