package com.seid.fetawa_.ui.fav

import android.content.Context
import androidx.lifecycle.ViewModel
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.db.DBDao
import com.seid.fetawa_.models.Question

class FavViewModel : ViewModel() {

    lateinit var db: DBDao
    fun setupDb(context: Context) {
        db = DB(context).dbDao()
    }
    fun isFav(uuid: String): Boolean {
        return db.getQuestionByUuid(uuid) != null
    }

    fun removeFav(question: Question) {
        db.deleteQuestion(question)
    }
    fun addFav(question: Question) {
        db.insertQuestion(question)
    }
}