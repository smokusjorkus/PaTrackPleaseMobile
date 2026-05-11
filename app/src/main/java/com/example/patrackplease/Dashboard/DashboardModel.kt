package com.example.patrackplease.Dashboard

import com.google.gson.annotations.SerializedName

data class DashboardModel(
    val completed: Int,
    val pending: Int,
    val overdue: Int,
    val total: Int
)