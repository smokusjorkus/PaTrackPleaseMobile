package com.example.patrackplease.Profile

import com.example.patrackplease.Profile.Dto.ProfileRequest
import com.example.patrackplease.Profile.Dto.UserResponseDto
import com.example.patrackplease.api.ApiService
import com.example.patrackplease.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileModel(
    private val apiService: ApiService, // <-- FIXED: Added the missing comma here!
    private val sessionManager: SessionManager
) {

    // FIXED: Dynamically grab the real email from the session manager
    private val currentUserEmail: String
        get() = sessionManager.getUserEmail() ?: ""

    // FIXED: Dynamically grab the real JWT token from the session manager
    private val authToken: String
        get() = "Bearer ${sessionManager.getToken() ?: ""}"

    suspend fun getProfileData(): Result<UserResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserByEmail(authToken, currentUserEmail)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfileInfo(request: ProfileRequest): Result<UserResponseDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateProfile(authToken, currentUserEmail, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update profile."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfilePhoto(imageFile: File): Result<UserResponseDto> = withContext(Dispatchers.IO) {
        try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val response = apiService.uploadPhoto(authToken, currentUserEmail, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to upload photo."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeProfilePhoto(): Result<UserResponseDto> = withContext(Dispatchers.IO) {
        try {
            // Passing null file to remove the photo
            val response = apiService.uploadPhoto(authToken, currentUserEmail, null)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to remove photo."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}