package com.example.patrackplease.Tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.R
import com.example.patrackplease.models.Task

class TaskAdapter(private var taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTaskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvTaskDesc: TextView = view.findViewById(R.id.tvTaskDesc)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val colorIndicator: View = view.findViewById(R.id.colorIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Use the updated variable names from your Task model
        holder.tvTaskTitle.text = task.taskName
        holder.tvTaskDesc.text = task.taskDescription
        holder.tvDate.text = task.dueDate
        holder.tvStatus.text = task.status

        // Optional: Change the left-side color strip based on status
        when (task.status.uppercase()) {
            "DONE" -> holder.colorIndicator.setBackgroundColor(0xFF00E676.toInt()) // Green
            "OVERDUE" -> holder.colorIndicator.setBackgroundColor(0xFFFF8A65.toInt()) // Red/Orange
            else -> holder.colorIndicator.setBackgroundColor(0xFFFFB300.toInt()) // Yellow/Upcoming
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }
}