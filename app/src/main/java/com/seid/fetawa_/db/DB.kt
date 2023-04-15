package com.seid.fetawa_.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.seid.fetawa_.models.Question

class DB(context: Context): SQLiteOpenHelper(context, "local_db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "create table if not exists questions (" +
                    "id text PRIMARY KEY," +
                    "answer text," +
                    "answered_by text," +
                    "category text," +
                    "posted_date text," +
                    "question text," +
                    "user text," +
                    "status INTEGER DEFAULT 0 CHECK(status in (0,1)))"
        )

    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        onCreate(db)
    }
    fun addQuestion(question: Question) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id", question.id)
            put("answer", question.answer)
            put("answered_by", question.answered_by)
            put("category", question.category)
            put("posted_date", question.posted_date)
            put("question", question.question)
            put("user", question.user)
            put("status", question.status)
        }
        db?.insert("questions", null, values)
        db.close()
    }
    fun removeQuestion(id: String) {
        val db = this.readableDatabase
        val selection = "id=?"
        val selectionArgs = arrayOf(id)
        db.delete("questions", selection, selectionArgs)
        db.close()
    }

    fun getQuestions(): List<Question> {
        val db = this.readableDatabase
        val query2 = "select * from questions"
        val c = db.rawQuery(query2, null)
        val questions = mutableListOf<Question>()
        with(c) {
            while (moveToNext()) {
                val question = Question(
                    this.getString(this.getColumnIndexOrThrow("answer")),
                    this.getString(this.getColumnIndexOrThrow("answered_by")),
                    this.getString(this.getColumnIndexOrThrow("category")),
                    this.getString(this.getColumnIndexOrThrow("id")),
                    this.getString(this.getColumnIndexOrThrow("posted_date")),
                    this.getString(this.getColumnIndexOrThrow("question")),
                    this.getString(this.getColumnIndexOrThrow("user")),
                    this.getInt(this.getColumnIndexOrThrow("status"))
                )
                questions.add(question)
            }
        }
        c.close()
        db.close()
        return questions
    }

    fun isFav(id: String): Boolean {
        val db = this.readableDatabase
        val query2 = "select * from questions where id = ?"
        val conditions = arrayOf(id)
        val c = db.rawQuery(query2, conditions)
        return c.moveToFirst()
    }
}