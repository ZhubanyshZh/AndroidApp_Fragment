package com.example.fragment.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.R
import com.example.fragment.db.SQLiteHelperTasks
import com.example.fragment.model.Task

class TaskAdapter(private var tasks: List<Task>, val db: SQLiteHelperTasks) :
    RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = tasks[position]
        holder.title.text = itemsViewModel.title
        holder.date.text = itemsViewModel.date

        holder.deleteTaskBtn.setOnClickListener {
            db.deleteTask(itemsViewModel.id)
            tasks = db.findByUserId(itemsViewModel.userId)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<Task>) {
        tasks = filteredList;
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList(userId: Int) {
        tasks = ArrayList()
        notifyDataSetChanged()
        db.clear(userId);
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskName)
        val date: TextView = itemView.findViewById(R.id.taskDate)
        val deleteTaskBtn: Button = itemView.findViewById(R.id.deleteTask)
    }
}