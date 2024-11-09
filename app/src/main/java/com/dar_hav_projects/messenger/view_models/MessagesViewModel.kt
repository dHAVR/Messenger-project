package com.dar_hav_projects.messenger.view_models

import android.os.Bundle
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
import com.dar_hav_projects.messenger.domens.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessagesViewModel(
    appComponent: AppComponent
) : ViewModel() {

    @Inject
    lateinit var networkActions: I_NetworkActions

    @Inject
    lateinit var messagesRepo: MessagesRepository

    val chatId = mutableStateOf("")

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

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

    suspend fun createMessageDB(message: Message) {
        messagesRepo.insertMessage(
            MessageEntity(
                message.messageId,
                message.chatId,
                message.senderId,
                message.content,
                message.timestamp,
                message.isRead
            )
        )
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
