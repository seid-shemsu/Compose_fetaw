package com.example.compose_test.models

data class Question(
    val answer: String?,
    val answered_by: String?,
    val category: String?,
    val id: String,
    val posted_date: String?,
    val question: String,
    val user: String? = null,
    val status: Int = 0,
)
