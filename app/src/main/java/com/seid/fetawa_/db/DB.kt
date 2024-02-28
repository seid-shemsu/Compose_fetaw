package com.seid.fetawa_.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.seid.fetawa_.models.Category
import com.seid.fetawa_.models.Question
import com.seid.fetawa_.models.Teacher
import com.seid.fetawa_.models.User
import com.seid.fetawa_.models.converters.Converters


@Database(entities = [Question::class, Category::class, Teacher::class, User::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DB: RoomDatabase() {

    private var INSTANCE: DB? = null

    open fun destroyInstance() {
        INSTANCE = null
    }

    companion object {
        @Volatile private var instance: DB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            DB::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()

    }

    abstract fun dbDao(): DBDao


}