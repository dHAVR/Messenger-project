package com.dar_hav_projects.messenger.domens.models

import android.provider.ContactsContract.CommonDataKinds.Nickname

data class UserData(
    val name: String,
    val surname: String,
    val nickname: String,
    val url: String
)
