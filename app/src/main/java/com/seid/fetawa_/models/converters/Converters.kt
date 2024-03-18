package com.seid.fetawa_.models.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seid.fetawa_.models.Teacher
import com.seid.fetawa_.models.User

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromUser(user: User): String {
        return gson.toJson(user)
    }

    @TypeConverter
    fun toUser(userString: String): User {
        return gson.fromJson(userString, User::class.java)
    }

    @TypeConverter
    fun fromTeacher(teacher: Teacher): String {
        return gson.toJson(teacher)
    }

    @TypeConverter
    fun toTeacher(teacherString: String): Teacher {
        return gson.fromJson(teacherString, Teacher::class.java)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(listString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(listString, listType)
    }
}