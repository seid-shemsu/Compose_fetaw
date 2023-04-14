package com.seid.fetawa_.utils

import android.content.Context
import com.seid.fetawa_.utils.Constants.SP_USER

object SPUtils {
    fun getPhone(context: Context): String {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("phone", "") ?: ""
    }

    fun getName(context: Context): String {
        val sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString("name", "Guest") ?: ""
    }

    fun getEmail(context: Context): String {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("email", "guest@user.com") ?: ""
    }

    fun signOut(context: Context) {
        val sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().clear().apply()
    }
}