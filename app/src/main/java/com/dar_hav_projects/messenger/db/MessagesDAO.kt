package com.dar_hav_projects.messenger.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dar_hav_projects.messenger.domens.models.Message
import kotlinx.coroutines.flow.Flow


@Dao
interface MessagesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp")
     fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Delete
    suspend fun deleteMessageByID(message: MessageEntity)

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

}