package com.example.patrackplease.Login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("username") val username: String?,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("data") val data: AuthData? = null
) {
    fun resolvedToken(): String? = token ?: data?.token

    fun resolvedEmail(): String? = email ?: user?.email ?: data?.email ?: data?.user?.email
}

data class AuthData(
    @SerializedName("token") val token: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("user") val user: UserData?
)


data class UserData(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
