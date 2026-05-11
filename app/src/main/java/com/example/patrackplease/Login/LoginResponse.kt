package com.example.patrackplease.Login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("email") val email: String?, // Now at the top level
    @SerializedName("username") val username: String?
)


data class UserData(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)