package com.example.patrackplease

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.patrackplease.Dashboard.DashboardActivity
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.utils.SessionManager

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)

        // The "Protected Route" Check
        if (sessionManager.isLoggedIn()) {
            // User has a token -> Send them to the Dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
        } else {
            // No token -> Kick them to the Login Screen
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Finish the SplashActivity so the user can't hit the back button and return to it
        finish()
    }
}