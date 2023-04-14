package com.seid.fetawa_.ui.my_questions

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.compose_test.models.Question
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seid.fetawa_.utils.Resource
import com.seid.fetawa_.utils.SPUtils
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

    fun getQuestions(context: Context) {
        viewModelScope.launch {
            questionsResponse.value = Resource.loading()
            FirebaseDatabase.getInstance().getReference("users_questions")
                .child(SPUtils.getPhone(context))
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val list: MutableList<Question> = mutableListOf()
                        snapshot.children.forEach {
                            try {
                                list.add(
                                    Question(
                                        answer = it.child("answer").value as String,
                                        answered_by = it.child("answered_by").value as String,
                                        category = it.child("category").value as String,
                                        id = it.child("id").value as String,
                                        posted_date = it.child("posted_date").value as String,
                                        question = it.child("question").value as String,
                                        user = it.child("user").value as String,
                                        status = (it.child("status").value as Long).toInt(),
                                    )
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        questionsResponse.value = Resource.success(list)
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }
    }
}