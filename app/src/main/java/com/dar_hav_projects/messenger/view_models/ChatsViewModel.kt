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
import com.dar_hav_projects.messenger.di.AppComponent
import com.dar_hav_projects.messenger.domens.actions.I_NetworkActions
import com.dar_hav_projects.messenger.domens.models.Chat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatsViewModel (
    appComponent: AppComponent
) : ViewModel() {

    @Inject
    lateinit var networkActions: I_NetworkActions


    init {
        appComponent.inject(this)
    }


    private var _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    var messageText = mutableStateOf("")

    suspend fun createChat(member2: String): Result<Boolean> {
        return networkActions.createChat(member2)
    }

    suspend fun deleteChat(chatID: String): Result<Boolean> {
        return networkActions.deleteChat(chatID)
    }

    suspend fun getChatName(chat: Chat): String {
        return try {
            val result = networkActions.getChatName(chat)
            result.getOrThrow()
        } catch (e: Exception) {
            "Unknown Chat Name"
        }
    }



    fun fetchChats() {
        viewModelScope.launch(Dispatchers.Default){
            networkActions.fetchChats()
                .onFailure {
                    Log.d("MyLoggg", "fetchChats() onFailure")
                    withContext(Dispatchers.Main) {
                        _chats.value = emptyList()
                    }
                }
                .onSuccess {
                    Log.d("MyLoggg", "fetchChats() onSuccess")
                withContext(Dispatchers.Main) {
                    Log.d("MyLoggg", "fetchChats() onSuccess $it")
                    _chats.value = it
                }
            }
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
                    return ChatsViewModel(appComponent) as T
                }
            }
    }
}