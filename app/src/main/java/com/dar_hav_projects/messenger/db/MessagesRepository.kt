package com.dar_hav_projects.messenger.db

import com.dar_hav_projects.messenger.domens.models.Message
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {
    suspend fun insertMessage(message: MessageEntity)
     fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>
}