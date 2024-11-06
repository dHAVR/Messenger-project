package com.dar_hav_projects.messenger.ui.screens.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.ui.small_composables.ContactCard
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.view_models.ContactsViewModel

@Composable
fun ContactsScreen(viewModel: ContactsViewModel) {

    val lifecycleOwner = LocalLifecycleOwner.current

    var contacts by remember {
        mutableStateOf(emptyList<Contact>())
    }
    viewModel.contacts.observe(lifecycleOwner, Observer { data ->
        if (data.isNotEmpty()) {
            contacts = data
        }
    })


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBarWithTitle("Contacts"){}
        }

    ){ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            itemsIndexed(contacts) { _, item ->
                ContactCard(item, viewModel){
                }
            }
        }
    }
}