package com.example.fragment.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.MainActivity
import com.example.fragment.R
import com.example.fragment.db.SQLiteHelperUsers
import com.example.fragment.fragments.UserTasks
import com.example.fragment.fragments.Users
import com.example.fragment.model.User

class UserAdapter(private var users: List<User>, val db: SQLiteHelperUsers) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_view_design, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = users[position]
        holder.userName.text = itemsViewModel.name

        holder.deleteBtn.setOnClickListener {
            db.deleteUser(itemsViewModel.id)
            users = db.getAllUsers()
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            val userFrame = UserTasks.newInstance()
            val bundle = Bundle()
            bundle.putInt("userId", itemsViewModel.id)
            bundle.putString("userName", itemsViewModel.name)
            userFrame.arguments = bundle


            try {
                val fragmentManager = (holder.itemView.context as? FragmentActivity)?.supportFragmentManager
                    ?: (holder.itemView.context as? Fragment)?.childFragmentManager

                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame, userFrame)
                    ?.commit()
            } catch (e: Exception) {
                Log.d("User Adapter Exception", "error: " + e.message.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filteredList: List<User>) {
        users = filteredList;
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList() {
        users = ArrayList()
        notifyDataSetChanged()
        db.clear();
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = this.itemView.findViewById(R.id.userName)
        val deleteBtn: Button = this.itemView.findViewById(R.id.deleteUserBtn)
    }
}