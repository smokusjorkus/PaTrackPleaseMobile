package com.example.patrackplease.api

import LoginRequest
import com.example.patrackplease.Dashboard.DashboardModel
import com.example.patrackplease.Login.LoginResponse
import com.example.patrackplease.Profile.Dto.ProfileRequest
import com.example.patrackplease.Profile.Dto.UserResponseDto
import com.example.patrackplease.Register.RegisterRequest.RegisterRequest
import com.example.patrackplease.models.Task
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

        @GET("/api/tasks")
        suspend fun getTasks(
                @Header("Authorization") token: String,
                @Query("email") email: String
        ): Response<List<Task>>

        @GET("api/tasks/metrics")
        suspend fun getDashboardMetrics(
                @Header("Authorization") token: String,
                @Query("email") email: String
        ): Response<DashboardModel>

        // Create a new task — POST /api/tasks/create?email=
        @POST("api/tasks")
        suspend fun createTask(
                @Header("Authorization") token: String,
                @Query("email") email: String,
                @Body task: Task
        ): Response<Task>

        // Edit existing task — PUT /api/tasks/edit/{id}?email=
        @PUT("api/tasks/edit/{id}")
        suspend fun updateTask(
                @Header("Authorization") token: String,
                @Path("id") taskId: Long,
                @Query("email") email: String,
                @Body task: Task
        ): Response<Task>

        // Delete a task — DELETE /api/tasks/{id}
        @DELETE("api/tasks/{id}")
        suspend fun deleteTask(
                @Header("Authorization") token: String,
                @Path("id") taskId: Long
        ): Response<Unit>

        // Mark as done — PUT /api/tasks/{id}/status?status=DONE
        @PUT("api/tasks/{id}/status")
        suspend fun updateTaskStatus(
                @Header("Authorization") token: String,
                @Path("id") taskId: Long,
                @Query("status") status: String
        ): Response<Task>

        @GET("api/users")
        suspend fun getAllUsers(
                @Header("Authorization") token: String
        ): Response<List<UserResponseDto>>

        // Get user by email (matches your Web App's initialization strategy)
        @GET("api/users/email")
        suspend fun getUserByEmail(
                @Header("Authorization") token: String,
                @Query("email") email: String
        ): Response<UserResponseDto>

        // Get user by ID
        @GET("api/users/{id}")
        suspend fun getUserById(
                @Header("Authorization") token: String,
                @Path("id") userId: Long
        ): Response<UserResponseDto>

        // Update profile text info (Username, Email, Password)
        @PUT("api/users/update")
        suspend fun updateProfile(
                @Header("Authorization") token: String,   // Added token for authorization consistency
                @Query("email") email: String,
                @Body request: ProfileRequest
        ): Response<UserResponseDto>                  // Wrapped in Response<> for safety

        // Upload / Delete profile picture
        @Multipart
        @POST("api/users/upload-photo")
        suspend fun uploadPhoto(
                @Header("Authorization") token: String,
                @Query("email") email: String,
                @Part file: MultipartBody.Part?          // Nullable in case they are clicking "Remove Photo"
        ): Response<UserResponseDto>

        // Delete user account
        @DELETE("api/users/{id}")
        suspend fun deleteUser(
                @Header("Authorization") token: String,
                @Path("id") userId: Long
        ): Response<ResponseBody>                   // Your backend returns a UserResponseDto
}