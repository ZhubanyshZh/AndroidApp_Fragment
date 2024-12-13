package com.example.fragment.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.fragment.model.Task
import java.io.Serializable
import java.time.LocalDateTime

class SQLiteHelperTasks(context: Context) : SQLiteOpenHelper(context, "tasks", null, 1),
    Serializable {

    private val TABLE_NAME_TASKS: String = "tasks"

    private val CREATE_TABLE_TASKS: String = """
        CREATE TABLE $TABLE_NAME_TASKS(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT,
                    date TEXT,
                    userId INTEGER REFERENCES users(id)) 
        """.trimIndent()

    private val DELETE_ALL_FROM_TABLE_TASKS: String = """
        DELETE FROM $TABLE_NAME_TASKS
    """.trimIndent()

    private val SELECT_ALL_QUERY_FROM_TABLE_TASKS: String = """
        SELECT * FROM $TABLE_NAME_TASKS
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_TASKS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_TASKS")
        onCreate(db)
    }

    fun addTask(title: String, date: String, userId: Int?): Long {
        try {
            val db: SQLiteDatabase = this.writableDatabase
            val values = ContentValues()
            values.put("title", title)
            values.put("date", date)
            values.put("userId", userId)

            val success = db.insert(TABLE_NAME_TASKS, null, values)
            db.close()
            return success
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "add task error: " + e.message.toString())
            return -1
        }
    }

    fun getAllTasks(): ArrayList<Task> {
        try {
            val tasks: ArrayList<Task> = ArrayList()
            val db = this.readableDatabase
            val cursor = db.rawQuery(SELECT_ALL_QUERY_FROM_TABLE_TASKS, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId"))
                    tasks.add(Task(id, title, date, userId))
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return tasks
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "error: ${e.message}")
            return ArrayList()
        }
    }

    fun updateTask(id: Int, title: String, date: Int): Int {
        try {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put("title", title)
            values.put("date", date)

            val success = db.update(TABLE_NAME_TASKS, values, "id=?", arrayOf(id.toString()))
            db.close()
            return success
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "error: ${e.message}")
            return -1
        }
    }

    fun deleteTask(id: Int) {
        try {
            val db = this.writableDatabase
            db.execSQL("DELETE FROM $TABLE_NAME_TASKS WHERE id = $id")
            db.close()
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "error: ${e.message}")
        }
    }

    fun clear(userId: Int?) {
        try {
            val db = this.writableDatabase
            db.execSQL("DELETE FROM $TABLE_NAME_TASKS WHERE userId = $userId")
            db.close()
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "error: ${e.message}")
        }
    }

    fun findByUserId(userId: Int?): ArrayList<Task> {
        try {
            val tasks: ArrayList<Task> = ArrayList()
            readableDatabase.use { db ->
                val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME_TASKS WHERE userId = ?", arrayOf(userId.toString()))
                cursor.use {
                    if (it.moveToFirst()) {
                        do {
                            val id = it.getInt(it.getColumnIndexOrThrow("id"))
                            val title = it.getString(it.getColumnIndexOrThrow("title"))
                            val date = it.getString(it.getColumnIndexOrThrow("date"))
                            val userId = it.getInt(it.getColumnIndexOrThrow("userId"))
                            tasks.add(Task(id, title, date, userId))
                        } while (it.moveToNext())
                    }
                }
            }
            return tasks
        } catch (e: Exception) {
            Log.d("SQLiteHelperTasks", "findByUserId error: " + e.message.toString())
        }
        return ArrayList()
    }
}