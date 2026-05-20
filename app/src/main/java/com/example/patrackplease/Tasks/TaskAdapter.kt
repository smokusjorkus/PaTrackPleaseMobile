package com.example.patrackplease.Tasks

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.patrackplease.R
import com.example.patrackplease.api.ApiClient
import com.example.patrackplease.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskAdapter(
    private var taskList: List<Task>,
    private val token: String,
    private val onEditClick: (Task) -> Unit,
    private val onAlarmClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTaskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val tvTaskDesc: TextView = view.findViewById(R.id.tvTaskDesc)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val colorIndicator: View = view.findViewById(R.id.colorIndicator)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.tvTaskTitle.text = task.taskName
        holder.tvTaskDesc.text = task.taskDescription
        holder.tvDate.text = task.dueDate
        holder.tvStatus.text = task.status

        when (task.status.uppercase()) {
            "DONE"    -> holder.colorIndicator.setBackgroundColor(0xFF00E676.toInt())
            "OVERDUE" -> holder.colorIndicator.setBackgroundColor(0xFFFF8A65.toInt())
            else      -> holder.colorIndicator.setBackgroundColor(0xFFFFB300.toInt())
        }

        holder.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.task_item_menu, popup.menu)

            popup.menu.findItem(R.id.action_done)?.isVisible =
                task.status.uppercase() != "DONE"

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_alarm  -> {
                        onAlarmClick(task)
                        true
                    }
                    R.id.action_edit   -> {
                        onEditClick(task)               // opens the bottom sheet in Activity
                        true
                    }
                    R.id.action_delete -> {
                        handleDelete(view.context, position)
                        true
                    }
                    R.id.action_done   -> {
                        handleDone(view.context, position)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = taskList.size

    fun updateTasks(newTasks: List<Task>) {
        taskList = newTasks
        notifyDataSetChanged()
    }

    private fun handleDelete(context: Context, position: Int) {
        val mutableList = taskList.toMutableList()
        val removed = mutableList.removeAt(position)
        taskList = mutableList
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, taskList.size)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.deleteTask("Bearer $token", removed.id)
            } catch (e: Exception) {
                Log.e("TASK_ERR", "Delete failed: ${e.message}")
            }
        }

        Toast.makeText(context, "Deleted: ${removed.taskName}", Toast.LENGTH_SHORT).show()
    }

    private fun handleDone(context: Context, position: Int) {
        val mutableList = taskList.toMutableList()
        val updated = mutableList[position].copy(status = "DONE")
        mutableList[position] = updated
        taskList = mutableList
        notifyItemChanged(position)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                ApiClient.apiService.updateTaskStatus("Bearer $token", updated.id, "DONE")
            } catch (e: Exception) {
                Log.e("TASK_ERR", "Status update failed: ${e.message}")
            }
        }

        Toast.makeText(context, "Marked as done: ${updated.taskName}", Toast.LENGTH_SHORT).show()
    }
}
