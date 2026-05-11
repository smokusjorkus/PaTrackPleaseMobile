package com.example.patrackplease.Dashboard.Dto;

public class DashboardMetricsDto {
    private int completedCount;
    private int pendingCount;
    private int overdueCount;
    private int totalCount;

    public DashboardMetricsDto(int completedCount, int pendingCount, int overdueCount, int totalCount) {
        this.completedCount = completedCount;
        this.pendingCount = pendingCount;
        this.overdueCount = overdueCount;
        this.totalCount = totalCount;
    }

    // Getters
    public int getCompletedCount() { return completedCount; }
    public int getPendingCount() { return pendingCount; }
    public int getOverdueCount() { return overdueCount; }
    public int getTotalCount() { return totalCount; }
}