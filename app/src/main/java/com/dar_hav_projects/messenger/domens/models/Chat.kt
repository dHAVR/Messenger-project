package com.dar_hav_projects.messenger.domens.models

data class Chat(
    val chatId: String = "",
    val chatName: String = "",
    val member1UId: String = "",
    val member2UId: String = "",
    val lastMessage: String? = null,
    val lastMessageTimestamp: Long? = null
)
