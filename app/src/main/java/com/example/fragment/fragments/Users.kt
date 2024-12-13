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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.MainActivity
import com.example.fragment.R
import com.example.fragment.adapter.UserAdapter
import com.example.fragment.client.UserServiceClient
import com.example.fragment.config.UserServiceConfig
import com.example.fragment.db.SQLiteHelperUsers
import com.example.fragment.dto.UserDto
import com.example.fragment.model.User
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class Users : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sqliteHelperUser = SQLiteHelperUsers(requireContext())
        val userServiceClient = UserServiceConfig.instance

        val usersRecyclerview: RecyclerView? = view.findViewById(R.id.usersList)

        val addUserBtn: Button? = view.findViewById(R.id.addUserBtn)
        val sortUsersBtn: Button? = view.findViewById(R.id.sortUsersBtn)
        val clearUsersBtn: Button? = view.findViewById(R.id.clearUsersBtn)
        val deleteUserBtn: Button? = view.findViewById(R.id.deleteUserBtn)

        var isAscendingUsers = true

        var users: ArrayList<User> = ArrayList()
        usersRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
        userServiceClient.getUser(27).enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    val userDto = response.body()
                    if (userDto != null) {
                        users.add(User(27, userDto.username))
                        Log.d("Users", "User added: ${userDto.username}")
                    } else {
                        Log.e("Users", "Response body is null")
                    }
                } else {
                    Log.e("Users", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Log.e("Users", "Request failed: ${t.message}")
            }
        })


        val userAdapter = UserAdapter(users, sqliteHelperUser)
        usersRecyclerview?.adapter = userAdapter

        addUserBtn?.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setTitle("Add User")
            builder.setView(input)
            builder.setPositiveButton("Add") { dialog, _ ->
                var userName = input.text.toString().trimIndent()
                if (userName != "") {
                    sqliteHelperUser.addUser(userName)
                    users = sqliteHelperUser.getAllUsers()
                    userAdapter.filterList(users)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        sortUsersBtn?.setOnClickListener() {
            if (isAscendingUsers) {
                userAdapter.filterList(users.sortedBy { it.name })
                isAscendingUsers = false
            } else {
                userAdapter.filterList(users.sortedByDescending { it.name })
                isAscendingUsers = true
            }
        }

        clearUsersBtn?.setOnClickListener() {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("You sure clear all users?")
            builder.setPositiveButton("YES") { dialog, _ ->
                sqliteHelperUser.clear()
                users = sqliteHelperUser.getAllUsers()
                userAdapter.filterList(users)
                dialog.dismiss()
            }
            builder.setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        deleteUserBtn?.setOnClickListener() {
            if (arguments != null) {
                sqliteHelperUser.deleteUser(arguments?.getInt("userId"))
                users = sqliteHelperUser.getAllUsers()
                userAdapter.filterList(users)
                Toast.makeText(requireContext(), "User deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Users()
    }
}