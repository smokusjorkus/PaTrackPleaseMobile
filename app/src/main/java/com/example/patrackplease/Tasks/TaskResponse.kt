package com.example.patrackplease.Tasks

data class TaskResponse(
    val id: Long,
    val taskName: String?,
    val taskDescription: String?,
    val dueDate: String?, // JSON usually sends dates as strings
    val status: String?
)
