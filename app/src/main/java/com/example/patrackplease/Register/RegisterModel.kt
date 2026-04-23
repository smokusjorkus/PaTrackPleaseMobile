package com.example.patrackplease.Register

import android.util.Patterns

class RegisterModel : RegisterContract.Model {

    override fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        callback: RegisterContract.Model.OnRegisterFinishedListener
    ) {
        when {
            name.isBlank() -> callback.onNameError("Name cannot be empty")
            email.isBlank() -> callback.onEmailError("Email cannot be empty")
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                callback.onEmailError("Enter a valid email")
            password.isBlank() -> callback.onPasswordError("Password cannot be empty")
            password.length < 6 -> callback.onPasswordError("Password must be at least 6 characters")
            confirmPassword.isBlank() ->
                callback.onConfirmPasswordError("Confirm password cannot be empty")
            password != confirmPassword ->
                callback.onConfirmPasswordError("Passwords do not match")
            else -> callback.onSuccess("Registration successful")
        }
    }
}