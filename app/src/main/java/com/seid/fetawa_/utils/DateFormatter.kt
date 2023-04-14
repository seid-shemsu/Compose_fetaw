package com.seid.fetawa_.utils

import java.util.concurrent.TimeUnit

object DateFormatter {
    fun getMoment(timestamp: Long?): String {
        val now = System.currentTimeMillis()
        val elapsedTimeMillis = now - (timestamp ?: now)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis)
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTimeMillis)
        val days = TimeUnit.MILLISECONDS.toDays(elapsedTimeMillis)
        val months = TimeUnit.MILLISECONDS.toDays(elapsedTimeMillis) / 30
        val years = TimeUnit.MILLISECONDS.toDays(elapsedTimeMillis) / 365

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 30 -> "$days day${if (days > 1) "s" else ""} ago"
            months < 12 -> "$months month${if (months > 1) "s" else ""} ago"
            else -> "$years year${if (years > 1) "s" else ""} ago"
        }
    }
}