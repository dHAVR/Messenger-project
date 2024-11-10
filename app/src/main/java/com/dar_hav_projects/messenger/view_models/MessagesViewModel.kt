package com.dar_hav_projects.messenger.view_models

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.dar_hav_projects.messenger.db.MessageEntity
import com.dar_hav_projects.messenger.db.MessagesRepository
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.domens.models.Message
import com.dar_hav_projects.messenger.encryption.I_Encryprion
import com.google.crypto.tink.subtle.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.PrivateKey
import java.security.PublicKey
import javax.inject.Inject

class MessagesViewModel(
    appComponent: AppComponent
) : ViewModel() {

    @Inject
    lateinit var networkActions: I_NetworkActions

    @Inject
    lateinit var messagesRepo: MessagesRepository

    @Inject
    lateinit var encryption: I_Encryprion

    val chatId = mutableStateOf("")

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _publicKey = MutableLiveData<PublicKey>()

    var messagesDB: Flow<List<MessageEntity>> = flowOf()

    init {
        appComponent.inject(this)
    }

    fun setChatId(newChatId: String) {
        chatId.value = newChatId
        messagesDB = messagesRepo.getMessagesForChat(newChatId)
    }

    suspend fun createMessage(message: Message) {
        networkActions.createMessage(message)
    }

    suspend fun deleteMessage(message: Message) {
        networkActions.deleteMessage(message)
    }

    fun listenForMessages(chatId: String) {
        viewModelScope.launch {
            networkActions.listenForMessages(chatId).collect { newMessages ->
                _messages.postValue(newMessages)
            }
        }
    }


    fun encryptMessage(message: String): String? {
        Log.d("MyLog", "encryptMessage publicKey: ${_publicKey.value} ")
        return _publicKey.value?.let { encryption.encryptMessage(message, it) }
    }


    fun encryptMessageSender(message: String): String? {
        val publicKey = encryption.getKeyPair(networkActions.getAlias())?.public
        return publicKey?.let { encryption.encryptMessage(message, it) }
    }


    fun  decryptMessage(encryptedMessage: String): String? {
        val byteArray = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val privateKey = encryption.getKeyPair(networkActions.getAlias())?.private
        return privateKey?.let { encryption.decryptMessage(byteArray, it) }
    }


   fun checkSender(userUID: String): Boolean {
        return networkActions.checkSender(userUID)
    }

    private fun getSender(): String{
        return networkActions.getAlias()
    }

    suspend fun createMessageDB(message: Message) {
        if(!networkActions.checkSender(message.senderId)){
            messagesRepo.insertMessage(
                MessageEntity(
                    null,
                    message.chatId,
                    message.senderId,
                    message.content,
                    message.timestamp,
                    message.isRead
                )
            )
        }
    }

    suspend fun createMessageDBSender(message: Message) {
            messagesRepo.insertMessage(
                MessageEntity(
                   null,
                    message.chatId,
                    getSender(),
                    message.content,
                    message.timestamp,
                    message.isRead
                )
            )
    }

    suspend fun deleteMessageByIdDB(message: MessageEntity){
        messagesRepo.deleteMessageByID(message)
    }

    suspend fun deleteMessagesForChat(chatId: String){
        messagesRepo.deleteMessagesForChat(chatId)
    }


    suspend fun getPublicKey(chatId: String) {
      networkActions.getPublicKey(chatId)
          .onSuccess {
              _publicKey.value = encryption.convertStringToPublicKey(it)
      }

    }

    companion object {
        fun provideFactory(
            appComponent: AppComponent,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return MessagesViewModel(appComponent) as T
                }
            }
    }
}
