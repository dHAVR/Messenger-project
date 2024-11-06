package com.dar_hav_projects.messenger.domens.actions

import android.net.Uri
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.domens.models.IsSignedEnum
import com.dar_hav_projects.messenger.domens.models.UserData

interface I_NetworkActions {
    suspend fun signUp(email: String, pass: String): Result<Boolean>
    suspend fun verifyEmail(): Result<Boolean>
    suspend fun signIn(email: String, pass: String): Result<IsSignedEnum>
    suspend fun isSignedAsync(): IsSignedEnum

    suspend fun saveUserData(nickname: String, name: String, surname: String, imageUri: Uri?): Result<Any>
    suspend fun fetchUserData():  Result<UserData?>

    suspend fun fetchChats():  Result<List<Chat>>
    suspend fun fetchContacts():  Result<List<Contact>>
}