package com.example.patrackplease.Profile.Dto

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("id") val id: Long,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)