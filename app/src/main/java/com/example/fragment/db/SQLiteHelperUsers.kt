package com.example.fragment.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fragment.model.User
import java.io.Serializable

class SQLiteHelperUsers(context: Context) : SQLiteOpenHelper(context, "users", null, 1), Serializable {
    private val TABLE_NAME_USERS: String = "users"

    private val CREATE_TABLE_USERS: String = """
        CREATE TABLE $TABLE_NAME_USERS(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT
                    )
    """.trimIndent()

    private val DELETE_ALL_FROM_TABLE_USERS: String = """
        DELETE FROM $TABLE_NAME_USERS
    """.trimIndent()

    private val SELECT_ALL_QUERY_FROM_TABLE_USERS: String = """
        SELECT * FROM $TABLE_NAME_USERS
    """.trimIndent()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USERS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_USERS")
        onCreate(db)
    }

    fun addUser(name: String): Long {
        val db: SQLiteDatabase = this.writableDatabase
        val values = ContentValues()
        values.put("name", name)

        val success = db.insert(TABLE_NAME_USERS, null, values)
        db.close()
        return success
    }

    fun getAllUsers(): ArrayList<User> {
        val purchaseList: ArrayList<User> = ArrayList()
        val db = this.readableDatabase
        val cursor = db.rawQuery(SELECT_ALL_QUERY_FROM_TABLE_USERS, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                purchaseList.add(User(id, name))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return purchaseList
    }

    fun updateUser(id: Int?, name: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put("name", name)

        val success = db.update(TABLE_NAME_USERS, values, "id=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    fun deleteUser(id: Int?): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_NAME_USERS, "id=?", arrayOf(id.toString()))
        db.close()
        return success
    }

    fun clear() {
        val db = this.writableDatabase
        db?.execSQL(DELETE_ALL_FROM_TABLE_USERS)
        db.close()
    }
}