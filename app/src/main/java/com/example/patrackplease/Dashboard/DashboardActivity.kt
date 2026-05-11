package com.example.patrackplease.Dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R
import com.example.patrackplease.Tasks.TaskActivity
import com.example.patrackplease.Tasks.TaskAdapter
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.models.Task
import com.example.patrackplease.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.*

class DashboardActivity : Activity() {

    private val activityScope = MainScope()
    private lateinit var sessionManager: SessionManager

    // Bottom Navigation & Header
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var ivProfile: ImageView

    // Metric Views
    private lateinit var tvCompletedCount: TextView
    private lateinit var tvPendingCount: TextView
    private lateinit var tvOverdueCount: TextView
    private lateinit var tvTotalCount: TextView
    private lateinit var tvGreeting: TextView

    // Card Views
    private lateinit var cvCompleted: MaterialCardView
    private lateinit var cvPending: MaterialCardView
    private lateinit var cvOverdue: MaterialCardView
    private lateinit var cvTotal: MaterialCardView

    // Pending Section Views
    private lateinit var tvEmptyState: TextView
    private lateinit var btnViewAll: Button
    private lateinit var rvPendingPreview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_dashboard)

        initViews()
        setupBottomNavigation()
        setupHeader()

        val email = sessionManager.getUserEmail()
        if (email != null) {
            fetchDashboardData(email)
        } else {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        // Make the View All button go to the Task page
        btnViewAll.setOnClickListener {
            startActivity(Intent(this, TaskActivity::class.java))
            finish()
        }
    }

    private fun initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        ivProfile = findViewById(R.id.ivProfile)

        tvCompletedCount = findViewById(R.id.tvCompletedCount)
        tvPendingCount = findViewById(R.id.tvPendingCount)
        tvOverdueCount = findViewById(R.id.tvOverdueCount)
        tvTotalCount = findViewById(R.id.tvTotalCount)
        tvGreeting = findViewById(R.id.tvGreeting)

        cvCompleted = findViewById(R.id.cvCompleted)
        cvPending = findViewById(R.id.cvPending)
        cvOverdue = findViewById(R.id.cvOverdue)
        cvTotal = findViewById(R.id.cvTotal)

        tvEmptyState = findViewById(R.id.tvEmptyState)
        btnViewAll = findViewById(R.id.btnViewAll)

        // Initialize the Preview RecyclerView
        rvPendingPreview = findViewById(R.id.rvPendingPreview)
        rvPendingPreview.layoutManager = LinearLayoutManager(this)
    }

    private fun setupHeader() {
        val email = sessionManager.getUserEmail()
        val displayName = email?.substringBefore("@") ?: "User"
        tvGreeting.text = "Hello, $displayName!"

        ivProfile.setOnClickListener {
            sessionManager.clearSession()
            navigateToLogin()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_dashboard
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> true
                R.id.nav_tasks -> {
                    startActivity(Intent(this, TaskActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
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

    private fun fetchDashboardData(email: String) {
        activityScope.launch {
            try {
                val token = sessionManager.getToken()
                val authHeader = "Bearer $token"

                // Fetch metrics and the full task list simultaneously
                val metricsDeferred = async(Dispatchers.IO) { ApiClient.apiService.getDashboardMetrics(authHeader, email) }
                val tasksDeferred = async(Dispatchers.IO) { ApiClient.apiService.getTasks(authHeader, email) }

                val metricsResponse = metricsDeferred.await()
                val tasksResponse = tasksDeferred.await()

                if (metricsResponse.isSuccessful && tasksResponse.isSuccessful) {
                    val m = metricsResponse.body()!!
                    val allTasks = tasksResponse.body()!!

                    // Filter for "Upcoming" tasks to show in the preview
                    val pendingPreview = allTasks.filter { it.status.equals("Upcoming", ignoreCase = true) }.take(3)

                    updateUI(m.completed, m.pending, m.overdue, m.total, pendingPreview)
                } else {
                    handleError("Server error.")
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    handleError("Network failed.")
                }
                Log.e("DASHBOARD_ERR", e.message ?: "Unknown error")
            }
        }
    }

    private fun updateUI(completed: Int, pendingCount: Int, overdue: Int, total: Int, pendingTasks: List<Task>) {
        tvCompletedCount.text = completed.toString()
        tvPendingCount.text = pendingCount.toString()
        tvOverdueCount.text = overdue.toString()
        tvTotalCount.text = total.toString()

        if (overdue > 0) {
            cvOverdue.setCardBackgroundColor(Color.parseColor("#FF4C4C"))
        } else {
            cvOverdue.setCardBackgroundColor(Color.parseColor("#FFCD53"))
        }

        // --- NEW PREVIEW LOGIC ---
        if (pendingTasks.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvPendingPreview.visibility = View.GONE
            btnViewAll.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvPendingPreview.visibility = View.VISIBLE
            btnViewAll.visibility = View.VISIBLE

            // USE THE DASHBOARD-SPECIFIC ADAPTER (NO BUTTONS)
            rvPendingPreview.adapter = DashboardTaskAdapter(pendingTasks)
        }
    }

    private fun handleError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}