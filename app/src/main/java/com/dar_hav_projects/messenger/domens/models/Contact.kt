package com.dar_hav_projects.messenger.domens.models

data class Contact(
    val userId: String = "",
    val name: String = "",
    val contactWith: String = "",
    val profileImageUrl: String? = null,
)
