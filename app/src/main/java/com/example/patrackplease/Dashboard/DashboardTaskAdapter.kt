package com.example.patrackplease.Dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.R
import com.example.patrackplease.models.Task

class DashboardTaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<DashboardTaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvTaskDesc)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val colorIndicator: View = view.findViewById(R.id.colorIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use the new no-button layout here
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_dashboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.tvTitle.text = task.taskName
        holder.tvDesc.text = task.taskDescription
        holder.tvDate.text = task.dueDate
        holder.tvStatus.text = task.status

        // Match the color strip to the status
        when (task.status.uppercase()) {
            "DONE" -> holder.colorIndicator.setBackgroundColor(0xFF00E676.toInt())
            "OVERDUE" -> holder.colorIndicator.setBackgroundColor(0xFFFF8A65.toInt())
            else -> holder.colorIndicator.setBackgroundColor(0xFFFFB300.toInt())
        }
    }

    override fun getItemCount(): Int = taskList.size
}