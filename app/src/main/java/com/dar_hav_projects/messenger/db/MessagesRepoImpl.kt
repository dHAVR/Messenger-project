package com.dar_hav_projects.messenger.db

import com.dar_hav_projects.messenger.domens.models.Message
import kotlinx.coroutines.flow.Flow

class MessagesRepoImpl(
    private val dao: MessagesDAO
): MessagesRepository {
    override suspend fun insertMessage(message: MessageEntity) {
        dao.insertMessage(message)
    }

    override  fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>> {
        return dao.getMessagesForChat(chatId)
    }

    override suspend fun deleteMessageByID(message: MessageEntity) {
        return dao.deleteMessageByID(message)
    }

    override suspend fun deleteMessagesForChat(chatId: String) {
        return dao.deleteMessagesForChat(chatId)
    }
}