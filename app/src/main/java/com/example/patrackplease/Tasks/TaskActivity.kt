package com.example.patrackplease.Tasks

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.Dashboard.DashboardActivity
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*

class TaskActivity : Activity() {

    // Network & Scope
    private lateinit var sessionManager: SessionManager
    private val activityScope = MainScope()

    // UI Components
    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        // --- AUTH GUARD ---
        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // --- INITIALIZE UI ---
        setupBottomNavigation() // <-- Notice this now calls the correct standard function
        setupRecyclerView()

        // --- FETCH DATA ---
        val email = sessionManager.getUserEmail()
        if (email != null) {
            fetchTasks(email)
        } else {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        rvTasks = findViewById(R.id.rvTasks)
        rvTasks.layoutManager = LinearLayoutManager(this)

        // Start with an empty list so the UI loads instantly
        taskAdapter = TaskAdapter(emptyList())
        rvTasks.adapter = taskAdapter
    }

    private fun fetchTasks(email: String) {
        activityScope.launch {
            try {
                val token = sessionManager.getToken()
                val authHeader = "Bearer $token"

                // Make the network call on the IO thread
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getTasks(authHeader, email)
                }

                if (response.isSuccessful && response.body() != null) {
                    val tasks = response.body()!!
                    // Pass the fresh data to the adapter
                    taskAdapter.updateTasks(tasks)

                    // Optional: Update your "7 tasks assigned to you" subtitle here
                    // findViewById<TextView>(R.id.tvPageSubtitle).text = "${tasks.size} tasks assigned to you"
                } else {
                    Toast.makeText(this@TaskActivity, "Failed to load tasks: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TaskActivity, "Network failed. Check connection.", Toast.LENGTH_SHORT).show()
                Log.e("TASK_ERR", e.message ?: "Unknown error")
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Tells the bar to highlight the "Tasks" icon right when the page loads
        bottomNavigation.selectedItemId = R.id.nav_tasks

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    // Go back to Dashboard
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_tasks -> {
                    // Already here, do nothing
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Navigating to Profile...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // CRITICAL: Prevent memory leaks when navigating away
        activityScope.cancel()
    }
}