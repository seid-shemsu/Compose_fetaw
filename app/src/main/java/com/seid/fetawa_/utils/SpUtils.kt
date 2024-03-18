package com.seid.fetawa_.utils

import android.content.Context

object SpUtils {
    fun saveDraft(context: Context, string: String) {
        context.getSharedPreferences("draft", Context.MODE_PRIVATE).edit()
            .putString("question", string).apply()
    }

    fun getDraft(context: Context): String {
        return context.getSharedPreferences("draft", Context.MODE_PRIVATE).getString("question", "")
            ?: ""
    }
}