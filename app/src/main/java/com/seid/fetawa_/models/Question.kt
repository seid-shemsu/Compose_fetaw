package com.seid.fetawa_.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey @ColumnInfo(name = "uuid") var uuid: String = "",
    @ColumnInfo(name = "answer") var answer: String = "",
    @ColumnInfo(name = "answered_by") var answeredBy: Teacher = Teacher(),
    @ColumnInfo(name = "answered_date") var answeredDate: Long = 0L,
    @ColumnInfo(name = "asked_by") var askedBy: User = User(),
    @ColumnInfo(name = "asked_date") var askedDate: Long = 0L,
    @ColumnInfo(name = "category") var category: Category = Category(),
    @ColumnInfo(name = "question") var question: String = "",
    @ColumnInfo(name = "references") var references: List<String> = ArrayList(),
    @ColumnInfo(name = "status") var status: Int = 0
): Serializable