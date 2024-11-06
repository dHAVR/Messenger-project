package com.dar_hav_projects.messenger.domens.models

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val timestamp: Long = 0L,
    val content: String = "",
    val isRead: Boolean = false,
)

