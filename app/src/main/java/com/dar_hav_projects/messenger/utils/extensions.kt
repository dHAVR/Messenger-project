package com.dar_hav_projects.messenger.utils

import android.content.Context
import com.dar_hav_projects.messenger.MessengerApp
import com.dar_hav_projects.messenger.di.AppComponent

fun Context.appComponent(): AppComponent {
    return if(this is MessengerApp){
        this.appComponent
    } else (this.applicationContext as MessengerApp).appComponent
}