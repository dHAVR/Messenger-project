package com.dar_hav_projects.messenger.ui.small_composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.db.MessageEntity
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.ui.theme.Blue300
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.meta2
import com.dar_hav_projects.messenger.utils.DateConvertor
import com.dar_hav_projects.messenger.view_models.MessagesViewModel


@Composable
fun MessageCard(
    item: MessageEntity,
    viewModel: MessagesViewModel,
    onClick: (MessageEntity) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shadow(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        expanded = true
                    }
                )
            },
        colors = if(viewModel.checkSender(item.senderId)) {
            CardDefaults.cardColors(containerColor = Blue300)
        }else{
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        },

        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = if(viewModel.checkSender(item.senderId)) {
                Modifier.padding(top = 10.dp, bottom = 10.dp, start = 25.dp, end = 10.dp)
            }else{
                Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 25.dp)

            },

            horizontalAlignment = if(viewModel.checkSender(item.senderId)) {
                Alignment.End
            }else{
                Alignment.Start
            },

        ) {
            Text(
                text = viewModel.decryptMessage(item.content) ?: "",
                style = body1,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = DateConvertor.formatDateTime(item.timestamp),
                style = meta2,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    Box{
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            DropdownMenuItem(onClick = {
                onClick(item)
                expanded = false
            },
                text = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(25.dp),
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(text = "Delete")
                    }

                }
            )
         }
        }
}
