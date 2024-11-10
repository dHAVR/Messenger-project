package com.dar_hav_projects.messenger.domens.models

data class UserData(
    val userUID: String = "",
    val name: String  = "",
    val surname: String  = "",
    val nickname: String  = "",
    val url: String  = "",
    val publicKey: String = ""
)
