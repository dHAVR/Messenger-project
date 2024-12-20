package com.dar_hav_projects.messenger.ui.small_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.ui.theme.Gray400
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.meta1
import com.dar_hav_projects.messenger.ui.theme.meta2
import com.dar_hav_projects.messenger.utils.DateConvertor
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.view_models.ChatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChatCard(
    item: Chat,
    viewModel: ChatsViewModel,
    onNavigate: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    var chatName by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        chatName = viewModel.getChatName(item)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .clickable {
                onNavigate("${Routes.Chat.name}/${item.chatId}")
            }
            .shadow(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp)
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chats),
                    contentDescription = "Chat Icon",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(5.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chatName,
                    style = body1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = item.lastMessage ?: "",
                    style = meta1,
                    color = Gray400
                )
            }

            Text(
                text = if (item.lastMessageTimestamp == 0L || item.lastMessageTimestamp == null){
                    ""
                }else{
                    DateConvertor.formatDateTime(item.lastMessageTimestamp ?: 0)
                },
                style = meta2,
                color = MaterialTheme.colorScheme.onSurface
            )

            IconButton(onClick = {
                onDelete(item.chatId)
            }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete chat",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}