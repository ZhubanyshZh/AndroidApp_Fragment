package com.example.fragment.fragments

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.MainActivity
import com.example.fragment.R
import com.example.fragment.adapter.TaskAdapter
import com.example.fragment.adapter.UserAdapter
import com.example.fragment.db.SQLiteHelperTasks
import com.example.fragment.db.SQLiteHelperUsers
import com.example.fragment.model.Task
import com.example.fragment.model.User

class UserTasks : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sqLiteHelperTasks = SQLiteHelperTasks(requireContext())
        val sqliteHelperUser = SQLiteHelperUsers(requireContext())
        val tasksRecyclerview: RecyclerView = view.findViewById(R.id.taskList)
        tasksRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        var tasks: ArrayList<Task> = ArrayList()
        val addTaskBtn: Button? = view.findViewById(R.id.addTaskBtn)
        val sortTasksBtn: Button? = view.findViewById(R.id.sortTasksBtn)
        val clearTasksBtn: Button? = view.findViewById(R.id.clearTasksBtn)
        val changeUserBtn: Button? = view.findViewById(R.id.changeUserBtn)
        val deleteUserBtn: Button? = view.findViewById(R.id.deleteUserBtn)
        val backBtn: Button? = view.findViewById(R.id.back)
        var isAscendingTasks = true
        var taskAdapter = TaskAdapter(ArrayList(), sqLiteHelperTasks)
        val usersRecyclerview: RecyclerView? = view.findViewById(R.id.usersList)
        usersRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
        var users = sqliteHelperUser.getAllUsers()
        var userAdapter = UserAdapter(users, sqliteHelperUser)
        usersRecyclerview?.adapter = userAdapter



        val userName: TextView = view.findViewById(R.id.userNameText)
        if(arguments != null) {
            try {
                userName.text = arguments?.getString("userName")
                tasks = sqLiteHelperTasks.findByUserId(arguments?.getInt("userId"))
                taskAdapter = TaskAdapter(tasks, sqLiteHelperTasks)
                tasksRecyclerview.adapter = taskAdapter
                Log.d("UserTasks", "tasks: $tasks")
            } catch (e: Exception) {
                Log.d("UserTasks", "tasks error: ${e.message}")
            }
        }

        backBtn?.setOnClickListener() {
            (activity as MainActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame, Users.newInstance())
                .commit()
        }

        addTaskBtn?.setOnClickListener() {
            try {
                val builder = AlertDialog.Builder(requireContext())
                val layout = LinearLayout(requireContext())
                layout.orientation = LinearLayout.VERTICAL

                val taskTitleInput = EditText(requireContext())
                val taskDateInput = EditText(requireContext())
                taskTitleInput.inputType = InputType.TYPE_CLASS_TEXT
                taskDateInput.inputType = InputType.TYPE_CLASS_DATETIME
                taskTitleInput.hint = "Task Title"
                taskDateInput.hint = "Task Date"

                layout.addView(taskTitleInput)
                layout.addView(taskDateInput)

                builder.setTitle("Add Task")
                builder.setView(layout)


                builder.setPositiveButton("ADD") { dialog, _ ->
                    var taskTitle = taskTitleInput.text.toString().trimIndent()
                    var taskDate = taskDateInput.text.toString().trimIndent()
                    if(taskTitle != "" && taskDate != "") {
                        if (arguments?.getInt("userId") != null) {
                            sqLiteHelperTasks.addTask(taskTitle, taskDate, arguments?.getInt("userId"))
                            tasks = sqLiteHelperTasks.findByUserId(arguments?.getInt("userId"))
                            taskAdapter.filterList(tasks)
                            Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        } else {
                            Log.d("UserTasks", "userTasksArguments is null " + arguments)
                            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            } catch (e: Exception) {
                Log.d("UserTasks", "error: " + e.message)
            }
        }

        clearTasksBtn?.setOnClickListener() {
            if (arguments != null) {
                sqLiteHelperTasks.clear(arguments?.getInt("userId"))
                tasks = sqLiteHelperTasks.findByUserId(arguments?.getInt("userId"))
                taskAdapter.filterList(tasks)
                Toast.makeText(requireContext(), "Tasks deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }

        sortTasksBtn?.setOnClickListener() {
            if (isAscendingTasks) {
                taskAdapter.filterList(tasks.sortedBy { it.date })
                isAscendingTasks = false
            } else {
                taskAdapter.filterList(tasks.sortedByDescending { it.date })
                isAscendingTasks = true
            }
        }


        changeUserBtn?.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setText(arguments?.getString("userName"))
            builder.setTitle("Change Name")
            builder.setView(input)
            builder.setPositiveButton("Change") { dialog, _ ->
                var name = input.text.toString().trimIndent()
                if (name != "") {
                    if (arguments?.getInt("userId") != null &&
                        arguments?.getString("userName") != null) {
                        sqliteHelperUser.updateUser(arguments?.getInt("userId"), name)
                        userName.text = name
                        Toast.makeText(requireContext(), "User name changed", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        deleteUserBtn?.setOnClickListener() {
            if (arguments != null) {
                sqliteHelperUser.deleteUser(arguments?.getInt("userId"))
                users = sqliteHelperUser.getAllUsers()
                userAdapter.filterList(users)
                (activity as MainActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, Users.newInstance())
                    .commit()
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = UserTasks()
    }
}
