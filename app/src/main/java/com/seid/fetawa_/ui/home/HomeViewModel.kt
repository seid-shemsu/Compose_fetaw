package com.seid.fetawa_.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.room.Dao
import com.seid.fetawa_.models.Question
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seid.fetawa_.db.DB
import com.seid.fetawa_.db.DBDao
import com.seid.fetawa_.models.Teacher
import com.seid.fetawa_.models.User
import com.seid.fetawa_.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)
    val category: MutableStateFlow<String> = MutableStateFlow(String())
    val questionsResponse: MutableStateFlow<Resource<List<Question>>> =
        MutableStateFlow(Resource.initial())
    val favResponse: MutableStateFlow<Resource<List<Question>>> =
        MutableStateFlow(Resource.initial())
    val categoryResponse: MutableStateFlow<Resource<List<String>>> =
        MutableStateFlow(Resource.initial())

    init {
        getCategories()
        getQuestions()
    }

    fun getQuestions() {
        viewModelScope.launch {
            questionsResponse.value = Resource.loading()
            FirebaseDatabase.getInstance().getReference("answered")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list: MutableList<Question> = mutableListOf()
                        snapshot.children.forEach {
                            try {
                                val question = it.getValue(Question::class.java)
                                Log.e("TAG", "-- ${snapshot.value}")
                                question?.let {
                                    list.add(question)
                                } ?: {
                                    list.add(
                                        Question(
                                            uuid = it.child("uuid").value as String,
                                            answer = it.child("answer").value as String,
                                            answeredBy = it.child("answeredBy").value as Teacher,
                                            answeredDate = it.child("answeredDate").value as Long,
                                            askedBy = it.child("askedBy").value as User,
                                            askedDate = it.child("askedDate").value as Long,
                                            category = it.child("category").value as String,
                                            question = it.child("question").value as String,
                                            //references = it.child("references").value as List<String>,
                                            status = (it.child("status").value as Long).toInt(),
                                        )
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        questionsResponse.value = Resource.success(list.reversed())
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
    }

    private fun getCategories() {
        viewModelScope.launch {
            categoryResponse.value = Resource.loading()
            FirebaseDatabase.getInstance().getReference("categories")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list: MutableList<String> = mutableListOf()
                        snapshot.children.forEach {
                            it.child("name").value?.let { cat ->
                                list.add(cat.toString())
                            }
                        }
                        categoryResponse.value = Resource.success(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
    }

    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("answered")

    fun getQuestionsByCategory(startAtKey: String = "") {

        viewModelScope.launch {
            questionsResponse.value = Resource.loading()
            val query =
                databaseReference.orderByChild("category").equalTo(category.value).limitToFirst(5)

            if (startAtKey.isNotEmpty()) {
                query.startAt(startAtKey)
            }
            Log.e("TAG", "${category.value}")
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: MutableList<Question> = mutableListOf()
                    snapshot.children.forEach {
                        try {
                            val question = it.getValue(Question::class.java)
                            Log.e("TAG", "-- ${snapshot.value}")
                            question?.let {
                                list.add(question)
                            } ?: {
                                list.add(
                                    Question(
                                        uuid = it.child("uuid").value as String,
                                        answer = it.child("answer").value as String,
                                        answeredBy = it.child("answeredBy").value as Teacher,
                                        answeredDate = it.child("answeredDate").value as Long,
                                        askedBy = it.child("askedBy").value as User,
                                        askedDate = it.child("askedDate").value as Long,
                                        category = it.child("category").value as String,
                                        question = it.child("question").value as String,
                                        //references = it.child("references").value as List<String>,
                                        status = (it.child("status").value as Long).toInt(),
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    questionsResponse.value = Resource.success(list.reversed())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }


    lateinit var db: DBDao

    fun setupDb(context: Context) {
        db = DB(context).dbDao()
    }

    suspend fun isFav(uuid: String): Boolean {
        return withContext(Dispatchers.IO) { db.getQuestionByUuid(uuid) != null }
    }

    suspend fun removeFav(question: Question) {
        withContext(Dispatchers.IO) {

            db.deleteQuestion(question)
        }
    }

    suspend fun addFav(question: Question) {
        withContext(Dispatchers.IO) {
            db.insertQuestion(question)
        }
    }

    suspend fun getFavorites() {
        return withContext(Dispatchers.IO) {
            favResponse.value = Resource.success(db.getAllQuestions())
        }
    }
}