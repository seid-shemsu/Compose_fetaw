package com.seid.fetawa_.utils

import android.content.Context

object SPUtils {
    fun getPhone(context: Context): String {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("phone", "sample") ?: "sample"
    }
    fun getName(context: Context): String {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("name", "User") ?: "User"
    }
    fun getEmail(context: Context): String {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("email", "user@gmail.com") ?: "User"
    }
}