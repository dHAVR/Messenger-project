package com.dar_hav_projects.messenger.ui.screens.home

import com.dar_hav_projects.messenger.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import com.dar_hav_projects.messenger.domens.models.Contact
import com.dar_hav_projects.messenger.domens.models.UserData
import com.dar_hav_projects.messenger.ui.small_composables.ContactCard
import com.dar_hav_projects.messenger.ui.small_composables.SearchContactCard
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithBackAction
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.utils.Routes
import com.dar_hav_projects.messenger.view_models.ContactsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContactScreen(viewModel: ContactsViewModel, onNavigate: (String) -> Unit) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var foundContacts by remember {
        mutableStateOf(emptyList<UserData>())
    }

    var searchInput by viewModel.searchInput
    val snackbarHostState = remember { SnackbarHostState() }


    viewModel.searchContacts.observe(lifecycleOwner, Observer { data ->
        if (data.isNotEmpty()) {
            foundContacts = data
        }
    })


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBarWithBackAction("Search for your friends!"){
                onNavigate(Routes.ContactsList.name)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

    ){ paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .fillMaxWidth(),
            ) {
                TextField(
                    value = searchInput,
                    onValueChange = {
                        searchInput =  it
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(end = 8.dp, start = 8.dp),
                    placeholder = { Text("Enter nickname") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                IconButton(onClick = {
                    viewModel.searchContacts(searchInput)
                    searchInput = ""

                }) {
                    Icon(
                        modifier = Modifier.size(25.dp),
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Search icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (foundContacts.isNotEmpty()){
                    itemsIndexed(foundContacts) { _, item ->
                        SearchContactCard(item){ item ->
                            coroutineScope.launch(Dispatchers.Main) {
                                viewModel.addContact(item).onSuccess {
                                    viewModel.addContactForFriend(item).onSuccess {
                                        onNavigate(Routes.ContactsList.name)
                                    }
                                }
                            }
                        }
                    }
                }else{
                    item {
                        Box(
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "User was not found")
                        }
                    }
                }

            }
        }

    }
}