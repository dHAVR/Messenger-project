package com.dar_hav_projects.messenger.ui.screens.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.ui.small_composables.ChatCard
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.MessagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChatsListScreen(
    viewModel: ChatsViewModel,
    messagesViewModel: MessagesViewModel,
    onNavigate: (String) -> Unit
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var chats by remember {
        mutableStateOf(emptyList<Chat>())
    }

    viewModel.chats.observe(lifecycleOwner, Observer { data ->
            chats = data
    })

    LaunchedEffect(Unit) {
        viewModel.fetchChats()
    }


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
                ChatCard(item, viewModel, onNavigate = onNavigate, onDelete = { chatID ->
                    coroutineScope.launch(Dispatchers.Default) {
                        viewModel.deleteChat(chatID)
                        messagesViewModel.deleteMessagesForChat(chatID)
                        withContext(Dispatchers.Main){
                            viewModel.fetchChats()
                        }
                    }
                })

            }
        }
    }
}