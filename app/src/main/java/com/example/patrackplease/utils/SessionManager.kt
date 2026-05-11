package com.example.patrackplease.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    // Save login status (Call this AFTER a successful Retrofit login)
    fun saveAuthToken(token: String) {
        prefs.edit().putString("USER_TOKEN", token).apply()
    }

    // Check if the user is logged in
    fun isLoggedIn(): Boolean {
        val token = prefs.getString("USER_TOKEN", null)
        return token != null // True if token exists, false if null
    }

    // Clear session (For logging out)
    fun clearSession() {
        // .clear() wipes EVERYTHING (Token, Email, etc.) at once. Much cleaner!
        prefs.edit().clear().apply()
    }

    // --- Save the User's Email ---
    fun saveUserEmail(email: String) {
        prefs.edit().putString("USER_EMAIL", email).apply()
    }

    // --- Get the User's Email ---
    fun getUserEmail(): String? {
        // Returns the email if it exists, or null if it doesn't
        return prefs.getString("USER_EMAIL", null)
    }

    // --- Get the Auth Token ---
    fun getToken(): String? {
        // Must match the exact key "USER_TOKEN" used in saveAuthToken
        return prefs.getString("USER_TOKEN", null)
    }
}