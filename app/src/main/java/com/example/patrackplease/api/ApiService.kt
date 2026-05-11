package com.example.patrackplease.api

import LoginRequest
import com.example.patrackplease.Dashboard.DashboardModel // Make sure you import this!
import com.example.patrackplease.Login.LoginResponse
import com.example.patrackplease.Register.RegisterRequest.RegisterRequest
import com.example.patrackplease.models.Task
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
        @POST("api/auth/login")
        suspend fun login(
                @Body request: LoginRequest
        ): Response<LoginResponse>

        @POST("api/auth/register")
        suspend fun register(
                @Body request: RegisterRequest
        ): Response<LoginResponse>

        // 1. For getting the full list of tasks (e.g., for a RecyclerView)
        @GET("/api/tasks") // Replace with your actual Spring Boot endpoint
        suspend fun getTasks(
                @Header("Authorization") token: String,
                @Query("email") email: String
        ): retrofit2.Response<List<Task>>

        // 2. NEW: For getting the summary numbers for the Dashboard screen
        @GET("api/tasks/metrics") // Pointing to our new specific endpoint
        suspend fun getDashboardMetrics(
                @Header("Authorization") token: String, // Your "Key"
                @Query("email") email: String           // Your "ID"
        ): Response<DashboardModel>
}