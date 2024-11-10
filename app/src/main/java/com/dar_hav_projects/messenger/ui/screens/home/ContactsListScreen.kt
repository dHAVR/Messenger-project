package com.dar_hav_projects.messenger.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.ui.small_composables.ContactCard
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.ContactsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsListScreen(
    chatsViewModel: ChatsViewModel,
    viewModel: ContactsViewModel,
    onNavigate: (String) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var contacts by remember {
        mutableStateOf(emptyList<Contact>())
    }

    viewModel.contacts.observe(lifecycleOwner, Observer { data ->
            contacts = data
    })

    LaunchedEffect(Unit) {
        viewModel.fetchContacts()
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = { Text("Contacts") },
                actions = {
                    IconButton(onClick = {
                        onNavigate(Routes.SearchContact.name)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = "Add Contact"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {

            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (contacts.isNotEmpty()) {
                    itemsIndexed(contacts) { _, item ->
                        ContactCard(item,
                            onDelete = { userUID ->
                            coroutineScope.launch(Dispatchers.Default) {
                                viewModel.deleteContact(userUID)
                                    withContext(Dispatchers.Main){
                                        viewModel.fetchContacts()
                                    }
                            }
                        }, onClick = { member2 ->
                            coroutineScope.launch(Dispatchers.Default) {
                                chatsViewModel.createChat(member2)
                                withContext(Dispatchers.Main){
                                    onNavigate(Routes.ChatsList.name)
                                }
                            }
                        })
                    }
                } else {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "You don't have contacts")
                        }
                    }
                }

            }
        }

    }
}