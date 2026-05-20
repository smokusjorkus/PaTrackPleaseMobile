package com.example.patrackplease.Profile.Dto

import com.google.gson.annotations.SerializedName

data class UserUpdateDto(
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("profileImageUrl") val profileImageUrl: String? = null
)