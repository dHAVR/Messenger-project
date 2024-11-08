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
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.ui.small_composables.ChatCard
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.view_models.ChatsViewModel

@Composable
fun ChatsListScreen(
    viewModel: ChatsViewModel,
    onNavigate: (String) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    var chats by remember {
        mutableStateOf(emptyList<Chat>())
    }
    viewModel.chats.observe(lifecycleOwner, Observer { data ->
        if (data.isNotEmpty()) {
            chats = data
        }
    })


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBarWithTitle("Chats"){}
        }

    ){ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            itemsIndexed(chats) { _, item ->
                ChatCard(item, viewModel, onNavigate = onNavigate)

            }
        }
    }
}