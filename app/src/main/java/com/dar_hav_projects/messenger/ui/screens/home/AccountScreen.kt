package com.dar_hav_projects.messenger.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import coil.compose.AsyncImage
import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.domens.models.Chat
import com.dar_hav_projects.messenger.domens.models.UserData
import com.dar_hav_projects.messenger.ui.small_composables.TopAppBarWithTitle
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.view_models.AccountViewModel
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.input.ObservableInputStream

@Composable
fun AccountScreen(viewModel: AccountViewModel) {

  val lifecycleOwner = LocalLifecycleOwner.current

  var accountData by remember {
    mutableStateOf(UserData())
  }

  viewModel.accountData.observe(lifecycleOwner, Observer { data ->
    accountData = data
  })

  LaunchedEffect(Unit) {
    viewModel.fetchAccountData()
  }

  Scaffold(
    contentWindowInsets = WindowInsets(0, 0, 0, 0),
    topBar = {
      TopAppBarWithTitle("Account"){}
    }

  ){ paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .padding(vertical = 30.dp, horizontal = 30.dp)
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
      horizontalAlignment = Alignment.Start,
    ) {

      AsyncImage(
        model = accountData.url,
        contentDescription = "Avatar",
        modifier = Modifier
          .size(100.dp)
          .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_contacts),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = accountData.name + " " + accountData.surname,
        style = body1,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "nickname: " + accountData.nickname,
        style = body1,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }

}