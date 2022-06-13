package com.app.helpoome

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "TolDB", null, 2) {
    companion object {
        var INSTANCE: DBHelper? = null
        fun getInstance(context: Context): DBHelper {
            if (INSTANCE == null)
                INSTANCE = DBHelper(context)
            return INSTANCE!!
        }
    }

    fun onGetDate(date: String): DataClass {
        val cursor =
            readableDatabase.rawQuery("SELECT * FROM toiletTBL WHERE name=\"$date\"",
                null)
        cursor.moveToFirst()
        val name = cursor.getString(0)
        val address = cursor.getString(1)
        cursor.close()
        return DataClass(name, address)
    }

    fun onFindDiary(name: String): Boolean {
        val cursor =
            readableDatabase.rawQuery("SELECT EXISTS (SELECT * FROM toiletTBL WHERE name=\"$name\" LIMIT 1) as success;",
                null)
        cursor.moveToFirst()
        if (cursor.getInt(0) == 1) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    fun onInsertDiary(date: String, content: String) {
        writableDatabase.execSQL(
            "INSERT INTO toiletTBL (name, address) Values (?, ?);",
            arrayOf(date, content)
        )
        writableDatabase.close()
    }

    fun onUpdateDiary(name: String, address: String) {
        writableDatabase.execSQL(
            "UPDATE toiletTBL SET content=\"$address\" WHERE date=\"$name\""
        )
    }

    fun onDeleteDiary(name: String) {
        writableDatabase.execSQL(
            "DELETE FROM toiletTBL WHERE date=\"$name\""
        )
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        p0!!.execSQL("CREATE TABLE toiletTBL (date VARCHAR(30) PRIMARY KEY, content VARCHAR(500) NOT NULL);")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS toiletTBL")
        onCreate(p0)
    }
}