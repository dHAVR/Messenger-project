package com.dar_hav_projects.messenger.ui.small_composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.view_models.ContactsViewModel

@Composable
fun ContactCard(
    item: Contact,
    onClick: (String) -> Unit,
    onDelete: (String) -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .shadow(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = item.profileImageUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(R.drawable.ic_contacts),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = item.name + " " + item.surname,
                            style = body1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "nickname: " + item.nickname,
                            style = body1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_drop_menu),
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = "Drop menu"
                    )
                }

            }
        Box{
            DropdownMenu(
                modifier = Modifier
                    .padding(end = 15.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(onClick = {
                    onDelete(item.userId)
                    expanded = false
                },
                    text = {
                       Row(
                           modifier = Modifier.fillMaxWidth(),
                           verticalAlignment = Alignment.CenterVertically
                       ) {
                           Icon(
                               modifier = Modifier.size(25.dp),
                               painter = painterResource(id = R.drawable.ic_delete_contact),
                               contentDescription = null)
                           Spacer(modifier = Modifier.width(20.dp))
                           Text(text = "Delete contact")
                       }

                    }
                )
                DropdownMenuItem(onClick = {
                    onClick(item.userId)
                    expanded = false
                },
                    text = {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_chats),
                                contentDescription = null)
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(text = "Create chat")
                        }

                    }
                )
            }
        }

        }


    }
}
