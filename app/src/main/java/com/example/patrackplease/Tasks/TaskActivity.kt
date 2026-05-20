package com.example.patrackplease.Tasks


import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import com.example.patrackplease.Profile.ProfileActivity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.Dashboard.DashboardActivity
import com.example.patrackplease.Login.LoginActivity
import com.example.patrackplease.R
import com.example.patrackplease.alarms.AlarmScheduler
import com.example.patrackplease.models.Task
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TaskActivity : Activity() {

    private lateinit var sessionManager: SessionManager
    private val activityScope = MainScope()
    private lateinit var alarmScheduler: AlarmScheduler

    private lateinit var rvTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var tvPageSubtitle: TextView
    private var pendingAlarmTask: Task? = null
    private var pendingAlarmTimeMillis: Long? = null

    companion object {
        private const val REQUEST_POST_NOTIFICATIONS = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        sessionManager = SessionManager(this)
        alarmScheduler = AlarmScheduler(this)
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        tvPageSubtitle = findViewById(R.id.tvPageSubtitle)   // initialize it here

        setupBottomNavigation()
        setupRecyclerView()

        findViewById<Button>(R.id.btnAddNewTask).setOnClickListener {
            openAddSheet()
        }

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

        val token = sessionManager.getToken() ?: ""
        taskAdapter = TaskAdapter(
            taskList = emptyList(),
            token = token,
            onEditClick = { task -> openEditSheet(task) },
            onAlarmClick = { task -> showReminderDatePicker(task) }
        )
        rvTasks.adapter = taskAdapter
    }

    private fun openAddSheet() {
        val token = sessionManager.getToken() ?: ""
        val email = sessionManager.getUserEmail() ?: ""
        TaskFormBottomSheet(
            token        = token,
            email        = email,
            existingTask = null,
            onSuccess    = { fetchTasks(email) }
        ).show(fragmentManager, "AddTask")
    }

    private fun openEditSheet(task: Task) {
        val token = sessionManager.getToken() ?: ""
        val email = sessionManager.getUserEmail() ?: ""
        TaskFormBottomSheet(
            token        = token,
            email        = email,
            existingTask = task,
            onSuccess    = { fetchTasks(email) }
        ).show(fragmentManager, "EditTask")
    }

    private fun fetchTasks(email: String) {
        activityScope.launch {
            try {
                val token = sessionManager.getToken()
                val authHeader = "Bearer $token"

                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getTasks(authHeader, email)
                }

                if (response.isSuccessful && response.body() != null) {
                    val tasks = response.body()!!
                    taskAdapter.updateTasks(tasks)
                    tvPageSubtitle.text = when (tasks.size) {
                        0    -> "No tasks assigned to you"
                        1    -> "1 task assigned to you"
                        else -> "${tasks.size} tasks assigned to you"
                    }
                } else {
                    Toast.makeText(
                        this@TaskActivity,
                        "Failed to load tasks: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@TaskActivity, "Network failed. Check connection.", Toast.LENGTH_SHORT).show()
                Log.e("TASK_ERR", e.message ?: "Unknown error")
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // 1. Set the active tab ONCE to match the current screen
        bottomNavigation.selectedItemId = R.id.nav_tasks

        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_tasks -> {
                    // Already on Tasks, do nothing
                    true
                }
                R.id.nav_profile -> {
                    // 2. Launch the ProfileActivity (Make sure to import it at the top of your file!)
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun showReminderDatePicker(task: Task) {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 5)
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showReminderTimePicker(task, calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showReminderTimePicker(task: Task, calendar: Calendar) {
        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                scheduleReminderWithPermissionCheck(task, calendar.timeInMillis)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    private fun scheduleReminderWithPermissionCheck(task: Task, triggerAtMillis: Long) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Please choose a future date and time.", Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            pendingAlarmTask = task
            pendingAlarmTimeMillis = triggerAtMillis
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_POST_NOTIFICATIONS
            )
            return
        }

        scheduleReminder(task, triggerAtMillis)
    }

    private fun scheduleReminder(task: Task, triggerAtMillis: Long) {
        alarmScheduler.scheduleTaskReminder(task, triggerAtMillis)
        val formattedTime = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
            .format(Date(triggerAtMillis))
        Toast.makeText(
            this,
            "Reminder set for ${task.taskName} on $formattedTime",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != REQUEST_POST_NOTIFICATIONS) {
            return
        }

        val task = pendingAlarmTask
        val triggerAtMillis = pendingAlarmTimeMillis
        pendingAlarmTask = null
        pendingAlarmTimeMillis = null

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
            task != null && triggerAtMillis != null
        ) {
            scheduleReminder(task, triggerAtMillis)
        } else {
            Toast.makeText(
                this,
                "Notification permission is needed for task reminders.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }
}
