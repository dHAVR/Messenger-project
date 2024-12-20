package com.dar_hav_projects.messenger.view_models

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Nickname
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

class ContactsViewModel(
appComponent: AppComponent
) : ViewModel() {

    @Inject
    lateinit var networkActions: I_NetworkActions

    private var _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private var _searchContacts = MutableLiveData<List<UserData>>()
    val searchContacts: LiveData<List<UserData>> = _searchContacts


    var searchInput = mutableStateOf("")

    fun searchContacts(nickname: String){
        viewModelScope.launch(Dispatchers.Default) {
            networkActions.searchContact(nickname)
                .onSuccess {
                    withContext(Dispatchers.Main) {
                        _searchContacts.value = it
                    }
                }
        }
    }

    suspend fun addContact(item: UserData): Result<Boolean> {
       return networkActions.addContact(item)
    }

    suspend fun addContactForFriend(item: UserData): Result<Boolean> {
        return networkActions.addContactForFriend(item)
    }

    suspend fun deleteContact(contactID: String): Result<Boolean> {
        return networkActions.deleteContact(contactID)
    }


    fun fetchContacts() {
        viewModelScope.launch(Dispatchers.Default){
            networkActions.fetchContacts()
                .onFailure {
                    withContext(Dispatchers.Main) {
                        _contacts.value = emptyList()
                    }
                }
                .onSuccess {
                withContext(Dispatchers.Main) {
                    _contacts.value = it
                }
            }
        }
    }



    init {
        appComponent.inject(this)
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
                    return ContactsViewModel(appComponent) as T
                }
            }
    }
}