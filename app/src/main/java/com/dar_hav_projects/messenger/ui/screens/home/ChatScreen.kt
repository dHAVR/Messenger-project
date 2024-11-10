package com.dar_hav_projects.messenger.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.domens.models.Message
import com.dar_hav_projects.messenger.ui.small_composables.MessageCard
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithBackAction
import com.dar_hav_projects.messenger.encryption.Encryption
import com.dar_hav_projects.messenger.utils.TimeProvider
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import com.dar_hav_projects.messenger.view_models.MessagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatsViewModel, chatID: String, messagesViewModel: MessagesViewModel) {

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()
    val messages by messagesViewModel.messages.observeAsState(initial = emptyList())

    val messagesDB = messagesViewModel.messagesDB.collectAsState(initial = emptyList())

    val content = viewModel.messageText.value

    LaunchedEffect(Unit) {
        Log.d("MyLog", " LaunchedEffect(Unit)")
        messagesViewModel.getPublicKey(chatID)
    }

    LaunchedEffect(messages) {
        Log.d("MyLog", " LaunchedEffect(messages)")
        messagesViewModel.setChatId(chatID)
        messagesViewModel.listenForMessages(chatID)
        if (messages.isNotEmpty()) {
            messages.let {
                messagesViewModel.createMessageDB(it.last())
            }
            messages.let { messagesViewModel.deleteMessage(it.last()) }

            coroutineScope.launch {
                listState.scrollToItem(messagesDB.value.size - 1)
            }
        }
    }

    LaunchedEffect(messagesDB.value) {
        Log.d("MyLog", " LaunchedEffect(messagesDB.value)")
        if (messagesDB.value.isNotEmpty()) {
            coroutineScope.launch {
                listState.scrollToItem(messagesDB.value.size - 1)
            }
        }
    }


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBarWithBackAction(title = "Chat") {

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
                state = listState
            ) {

                itemsIndexed(messagesDB.value) { _, item ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (messagesViewModel.checkSender(item.senderId)) {
                            Alignment.End
                        } else {
                            Alignment.Start
                        }
                    ) {
                        MessageCard(item, messagesViewModel) { message ->
                            coroutineScope.launch(Dispatchers.Default) {
                                messagesViewModel.deleteMessageByIdDB(message)
                            }
                        }
                    }


                }
            }
            Row(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = viewModel.messageText.value,
                    onValueChange = {
                        viewModel.messageText.value = it
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
                                messagesViewModel.encryptMessage(content) ?: "",
                                false
                            )
                        )

                        messagesViewModel.createMessageDBSender(
                            Message(
                                "",
                                chatID,
                                "",
                                TimeProvider.getCurrentTime(),
                                messagesViewModel.encryptMessageSender(content) ?: "",
                                false
                            )
                        )

                    }
                    viewModel.messageText.value = ""
                }
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }


    }
}