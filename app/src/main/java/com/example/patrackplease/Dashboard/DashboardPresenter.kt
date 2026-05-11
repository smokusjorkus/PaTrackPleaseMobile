package com.example.patrackplease.Dashboard

import com.example.patrackplease.api.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardPresenter(
    private var view: DashboardContract.View?,
    private val apiService: ApiService // Pass your Retrofit ApiService here
) : DashboardContract.Presenter {

    // Setup Coroutines for network calls
    private val presenterJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + presenterJob)

    override fun fetchDashboardData(userId: Long) {
        view?.showLoading()

        // Launch a coroutine to make the API call in the background
        coroutineScope.launch {
            try {
                // TODO: Ensure your ApiService has a method that returns a DashboardModel
                // val response = apiService.getDashboardMetrics(userId)

                // --- SIMULATED DATA FOR NOW ---
                val isSuccessful = true // Replace with response.isSuccessful

                if (isSuccessful) {
                    // val data = response.body()
                    val data = DashboardModel(completed = 12, pending = 3, overdue = 2, total = 17)

                    view?.hideLoading()
                    view?.displayMetrics(data.completed, data.pending, data.overdue, data.total)
                    view?.updatePendingTasksState(data.pending)
                } else {
                    view?.hideLoading()
                    view?.showError("Failed to fetch dashboard data")
                }

            } catch (e: Exception) {
                view?.hideLoading()
                view?.showError(e.localizedMessage ?: "An unexpected error occurred")
            }
        }
    }

    override fun onDestroy() {
        // Drop the reference to the View and cancel network calls if the Activity is destroyed
        view = null
        presenterJob.cancel()
    }
}