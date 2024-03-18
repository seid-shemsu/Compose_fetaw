package com.seid.fetawa_.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.models.User


@Dao
interface DBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestion(question: Question)

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): List<Question>

    @Query("SELECT * FROM questions WHERE uuid = :uuid")
    fun getQuestionByUuid(uuid: String): Question?

    // Update
    @Update
    fun updateQuestion(question: Question)

    // Delete
    @Delete
    fun deleteQuestion(question: Question)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): User?
}