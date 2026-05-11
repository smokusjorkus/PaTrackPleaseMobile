package com.example.patrackplease.models

data class Task(
    val id: Long,
    val taskName: String,        // Matches logs
    val taskDescription: String, // Matches logs
    val dueDate: String,
    val status: String
)