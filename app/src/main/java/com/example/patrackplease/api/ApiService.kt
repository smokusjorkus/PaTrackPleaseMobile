package com.example.patrackplease.api

import LoginRequest
import com.example.patrackplease.Login.LoginResponse
import com.example.patrackplease.Register.RegisterRequest.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
        @POST("api/auth/login")
        suspend fun login(
            @Body request: LoginRequest // Changed from LoginModel to LoginRequest
        ): Response<LoginResponse>

        @POST("api/auth/register")
        suspend fun register(
        @Body request: RegisterRequest
        ): Response<LoginResponse>
    }

