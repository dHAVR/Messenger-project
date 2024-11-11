package com.dar_hav_projects.messenger.ui.modelsUI

import android.content.Context
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.utils.Routes


sealed class BottomNavItem(
    val title: String,
    val icon: Int,
    val route: String
){
    data class ChatsItem(val context: Context) : BottomNavItem(
       "Chats",
        R.drawable.ic_chats,
        Routes.ChatsList.name
    )

    data class SettingsItem (val context: Context): BottomNavItem(
        "Account",
        R.drawable.ic_settings,
        Routes.Account.name
    )
    data class ContactsItem(val context: Context): BottomNavItem(
        "Contacts",
        R.drawable.ic_contacts,
        Routes.ContactsList.name
    )
}


