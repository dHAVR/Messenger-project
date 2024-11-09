package com.dar_hav_projects.messenger.utils

object TimeProvider {
    fun getCurrentTime(): Long {
        return System.currentTimeMillis()
    }
}