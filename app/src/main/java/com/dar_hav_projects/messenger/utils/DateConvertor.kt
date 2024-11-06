package com.dar_hav_projects.messenger.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateConvertor {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())


    private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())


    fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        return timeFormat.format(date)
    }

    fun formatDateTime(timestamp: Long): String {
        val date = Date(timestamp)
        return dateTimeFormat.format(date)
    }
}