package com.example.patrackplease.Dashboard

interface DashboardContract {

    // The Activity will implement this to handle UI updates
    interface View {
        fun showLoading()
        fun hideLoading()
        fun displayMetrics(completed: Int, pending: Int, overdue: Int, total: Int)
        fun updatePendingTasksState(pendingTaskCount: Int)
        fun showError(message: String)
    }

    // The Presenter will implement this to handle data/business logic
    interface Presenter {
        fun fetchDashboardData(userId: Long)
        fun onDestroy() // Cleans up to prevent memory leaks
    }
}