package com.example.patrackplease.Register.RegisterRequest

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val confirmPassword: String, // <-- YOU MUST ADD THIS
    val username: String? = null
)