package com.dar_hav_projects.messenger.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.domens.models.Message
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithBackAction
import com.dar_hav_projects.messenger.utils.TimeProvider
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.MessagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatsViewModel, chatID: String, messagesViewModel: MessagesViewModel){

    val coroutineScope = rememberCoroutineScope()
    val messages by messagesViewModel.messages.observeAsState(initial = emptyList())
    Log.d("MyLog", "ChatScreen ${messages.isEmpty()}")

    val itemsList = messagesViewModel.messagesDB.collectAsState(initial = emptyList())
    Log.d("MyLog", "Messages in db : ${itemsList.value} ")

    LaunchedEffect(messages) {
        Log.d("MyLog", "LaunchedEffect(chatID)")
        messagesViewModel.setChatId(chatID)
        messagesViewModel.listenForMessages(chatID)
        if (messages.isNotEmpty()) {
            Log.d("MyLog", " (messages.isNotEmpty()")
            messages.let {
                Log.d("MyLog", " messages?.let createMessageDB")
                messagesViewModel.createMessageDB(it.last())
            }
            messages.let { messagesViewModel.deleteMessage(it.last()) }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBarWithBackAction(title = "Chat"){

            }
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(4f)
                    .padding(vertical = 8.dp),
            ) {
                val messages = listOf(
                    Pair("Taya", "Hi!"),
                    Pair("Daryna", "Hey")
                )

                items(messages) { (sender, message) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalAlignment = if (sender == "Daryna") Alignment.Start else Alignment.End
                    ) {
                        Text(
                            text = sender,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = message,
                            modifier = Modifier
                                .background(
                                    color = if (sender == "Daryna") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = viewModel.messageText.value,
                    onValueChange = {
                        viewModel.messageText.value =  it
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp, start = 8.dp),
                    placeholder = { Text("Type a message") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                IconButton(onClick = {
                    coroutineScope.launch(Dispatchers.Default) {
                        messagesViewModel.createMessage(
                            Message(
                                "",
                                chatID,
                                "",
                                TimeProvider.getCurrentTime(),
                                viewModel.messageText.value,
                                false
                            )
                        )
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }


    }
}