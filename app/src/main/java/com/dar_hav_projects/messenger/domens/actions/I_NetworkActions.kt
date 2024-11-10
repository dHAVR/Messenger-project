package com.dar_hav_projects.messenger.domens.actions

import android.net.Uri
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.domens.models.Message
import com.dar_hav_projects.messenger.domens.models.UserData
import kotlinx.coroutines.flow.Flow
import java.security.PublicKey

interface I_NetworkActions {
    suspend fun signUp(email: String, pass: String): Result<Boolean>
    suspend fun verifyEmail(): Result<Boolean>
    suspend fun signIn(email: String, pass: String): Result<IsSignedEnum>
    suspend fun isSignedAsync(): IsSignedEnum

    suspend fun saveUserData(nickname: String,name: String,surname: String,imageUri: Uri?, publicKey: PublicKey?): Result<Any>
    suspend fun fetchUserData():  Result<UserData?>
    suspend fun fetchUserDataByID(userId: String):  Result<UserData?>
    suspend fun getPublicKey(chatID: String): Result<String>

    suspend fun fetchChats():  Result<List<Chat>>
    suspend fun createChat(member2: String): Result<Boolean>
    suspend fun getChatName(item: Chat): Result<String>
    suspend fun deleteChat(chatId: String): Result<Boolean>

    suspend fun listenForMessages(chatID: String): Flow<List<Message>>
    suspend fun createMessage(message: Message): Result<Boolean>
    suspend fun deleteMessage(message: Message): Result<Boolean>
    fun checkSender(userUID: String): Boolean

    fun getAlias(): String


    suspend fun fetchContacts():  Result<List<Contact>>
    suspend fun addContact(item: UserData): Result<Boolean>
    suspend fun searchContact(nickname: String): Result<List<UserData>>
    suspend fun deleteContact(contactId: String): Result<Boolean>
    suspend fun addContactForFriend(item: UserData): Result<Boolean>
}