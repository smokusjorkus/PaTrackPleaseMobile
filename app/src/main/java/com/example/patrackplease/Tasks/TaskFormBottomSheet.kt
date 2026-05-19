package com.example.patrackplease.Tasks

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.patrackplease.R
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@SuppressLint("ValidFragment")
class TaskFormBottomSheet(
    private val token: String,
    private val email: String,
    private val existingTask: Task? = null,
    private val onSuccess: () -> Unit
) : DialogFragment() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_task_form, container, false)

        val tvFormTitle   = view.findViewById<TextView>(R.id.tvFormTitle)
        val etTaskName    = view.findViewById<EditText>(R.id.etTaskName)
        val etTaskDesc    = view.findViewById<EditText>(R.id.etTaskDesc)
        val etDueDate     = view.findViewById<EditText>(R.id.etDueDate)
        val spinnerStatus = view.findViewById<Spinner>(R.id.spinnerStatus)
        val btnClose      = view.findViewById<ImageButton>(R.id.btnClose)
        val btnCancel     = view.findViewById<Button>(R.id.btnCancel)
        val btnSave       = view.findViewById<Button>(R.id.btnSave)

        // --- Spinner setup ---
        val statuses = listOf("PENDING", "DONE")
        val spinnerAdapter = ArrayAdapter(
            activity,
            android.R.layout.simple_spinner_item,
            statuses
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = spinnerAdapter

        // --- Date picker ---
        etDueDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                activity,
                { _, year, month, day ->
                    etDueDate.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // --- Edit mode: pre-fill fields ---
        if (existingTask != null) {
            tvFormTitle.text = "Edit Task"
            btnSave.text     = "Save Changes"
            etTaskName.setText(existingTask.taskName)
            etTaskDesc.setText(existingTask.taskDescription)
            etDueDate.setText(existingTask.dueDate)
            val statusIndex = statuses.indexOf(existingTask.status.uppercase())
            if (statusIndex >= 0) spinnerStatus.setSelection(statusIndex)
        } else {
            tvFormTitle.text = "Add New Task"
            btnSave.text     = "Add Task"
        }

        btnClose.setOnClickListener { dismiss() }
        btnCancel.setOnClickListener { dismiss() }

        btnSave.setOnClickListener {
            val taskName = etTaskName.text.toString().trim()
            val taskDesc = etTaskDesc.text.toString().trim()
            val dueDate  = etDueDate.text.toString().trim()
            val status   = spinnerStatus.selectedItem.toString()

            if (taskName.isEmpty() || taskDesc.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(activity, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            btnSave.text      = "Saving..."

            scope.launch {
                try {
                    val authHeader = "Bearer $token"
                    val response = withContext(Dispatchers.IO) {
                        if (existingTask != null) {
                            ApiClient.apiService.updateTask(
                                authHeader,
                                existingTask.id,
                                email,
                                existingTask.copy(
                                    taskName        = taskName,
                                    taskDescription = taskDesc,
                                    dueDate         = dueDate,
                                    status          = status
                                )
                            )
                        } else {
                            ApiClient.apiService.createTask(
                                authHeader,
                                email,
                                Task(
                                    id              = 0,
                                    taskName        = taskName,
                                    taskDescription = taskDesc,
                                    dueDate         = dueDate,
                                    status          = status
                                )
                            )
                        }
                    }

                    if (response.isSuccessful) {
                        Toast.makeText(
                            activity,
                            if (existingTask != null) "Task updated ✓" else "Task added ✓",
                            Toast.LENGTH_SHORT
                        ).show()
                        onSuccess()
                        dismiss()
                    } else {
                        Toast.makeText(activity, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                        btnSave.isEnabled = true
                        btnSave.text = if (existingTask != null) "Save Changes" else "Add Task"
                    }
                } catch (e: Exception) {
                    Toast.makeText(activity, "Network error.", Toast.LENGTH_SHORT).show()
                    btnSave.isEnabled = true
                    btnSave.text = if (existingTask != null) "Save Changes" else "Add Task"
                }
            }
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}