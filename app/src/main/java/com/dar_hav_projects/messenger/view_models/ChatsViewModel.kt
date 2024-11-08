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
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.domens.models.UserData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatsViewModel (
    appComponent: AppComponent
) : ViewModel() {

    @Inject
    lateinit var networkActions: I_NetworkActions

    private var _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    var messageText = mutableStateOf("")

    suspend fun createChat(member2: String): Result<Boolean> {
        return networkActions.createChat(member2)
    }


    init {
        appComponent.inject(this)
        Log.d("MyLog", "INIT")
        viewModelScope.launch(Dispatchers.Default){
            networkActions.fetchChats().onSuccess {
               withContext(Dispatchers.Main) {
                   _chats.value = it
                   Log.d("MyLog", "chats: ${_chats.value}")
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