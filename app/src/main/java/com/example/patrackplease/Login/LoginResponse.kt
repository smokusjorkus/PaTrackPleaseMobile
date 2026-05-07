package com.example.patrackplease.Login

data class LoginResponse(
    val token: String,
    val message: String,
    val user: UserData
)

data class UserData(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)