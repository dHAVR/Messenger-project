package com.dar_hav_projects.messenger.view_models

import android.net.Uri
import android.os.Bundle
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
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.domens.models.User
import com.dar_hav_projects.messenger.domens.models.UserData
import com.dar_hav_projects.messenger.encryption.I_Encryprion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SignViewModel(
    appComponent: AppComponent
) : ViewModel() {

    @Inject lateinit var networkActions: I_NetworkActions

    @Inject
    lateinit var encryption: I_Encryprion

    var user = mutableStateOf(User())
        private set

    var confirmPassword = mutableStateOf("")
        private set

    var showLoadingDialog = mutableStateOf(false)
        private set

    private var _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private var _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private var _surname = MutableStateFlow("")
    val surname = _surname.asStateFlow()

    private var _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData



    init {
        appComponent.inject(this)
        viewModelScope.launch(Dispatchers.Default) {
            fetchUserData()
        }
    }

    fun updateUser(user: User){ this.user.value = user }
    fun updateConfirmPassword(newConfirmPass: String){ confirmPassword.value = newConfirmPass }

    fun isEmailValid() = android.util.Patterns.EMAIL_ADDRESS.matcher(user.value.email).matches()
    fun isPasswordValid(input: String):Boolean {
        if (input.length < 8) return false
        val letterRegex = Regex("[a-zA-Z]")
        if (!letterRegex.containsMatchIn(input)) return false

        val digitRegex = Regex("[0-9]")
        if (!digitRegex.containsMatchIn(input)) return false

        val specialCharRegex = Regex("[^a-zA-Z0-9]")
        if (!specialCharRegex.containsMatchIn(input)) return false

        return true
    }

    suspend fun signIn(): Result<IsSignedEnum> {
        showLoadingDialog.value = true
        delay(1500)
        val (email, pass) = user.value
        return networkActions.signIn(email, pass)
    }

    suspend fun verifyEmail(): Result<Boolean> {
        return networkActions.verifyEmail()
    }

    suspend fun signUp(): Result<Boolean> {
        showLoadingDialog.value = true
        val (email, pass) = user.value
        return networkActions.signUp(email, pass)
    }

    fun closeLoadingDialog(){
        showLoadingDialog.value = false
    }

    fun updateNickname(newNickname: String) {
        _nickname.value = newNickname
    }
    fun updateName(newName: String) {
        _name.value = newName
    }

    fun updateSurname(newSurname: String) {
        _surname.value = newSurname
    }


    suspend fun saveUserData(uri: Uri?): Result<Any>{
        showLoadingDialog.value = true
        val publicKey = encryption.generateAndSaveKeyPair(networkActions.getAlias())?.public
        return networkActions.saveUserData(nickname.value, name.value, surname.value, uri, publicKey)
    }

    private suspend fun fetchUserData() {
       networkActions.fetchUserData()
           .onSuccess {
               withContext(Dispatchers.Main) {
                   _userData.value = it
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
                    return SignViewModel(appComponent) as T
                }
            }
    }

}
