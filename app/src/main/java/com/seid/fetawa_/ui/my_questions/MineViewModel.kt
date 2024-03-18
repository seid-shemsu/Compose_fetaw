package com.seid.fetawa_.ui.my_questions

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.seid.fetawa_.models.Question
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seid.fetawa_.models.Teacher
import com.seid.fetawa_.models.User
import com.seid.fetawa_.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MineViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.IO + viewModelJob)

    val questionsResponse: MutableStateFlow<Resource<List<Question>>> =
        MutableStateFlow(Resource.initial())
    val list = mutableListOf<Question>()

    fun getQuestions(context: Context, uuid: String) {
        viewModelScope.launch {
            questionsResponse.value = Resource.loading()
            FirebaseDatabase.getInstance().getReference("users_questions")
                .child(uuid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            try {
                                val question = it.getValue(Question::class.java)
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
    fun getList(index: Int) {
        viewModelScope.launch {
            questionsResponse.value = Resource.loading()
            when (index) {
                0 -> {
                    questionsResponse.value = Resource.success(list.reversed())
                }
                1 -> {
                    questionsResponse.value = Resource.success(list.reversed().filter { it.answer.isEmpty() })
                }
                2 -> {
                    questionsResponse.value = Resource.success(list.reversed().filter { it.answer.isNotEmpty() })
                }
            }
        }
    }
}