package com.dar_hav_projects.messenger.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val chatId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)

