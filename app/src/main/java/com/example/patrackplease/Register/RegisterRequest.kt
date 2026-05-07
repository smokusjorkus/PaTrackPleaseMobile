package com.example.patrackplease.Register.RegisterRequest

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val username: String? = null // Optional, as it's nullable in your backend
)