package com.dar_hav_projects.messenger.ui.small_composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.ui.theme.MessengerTheme
import com.dar_hav_projects.messenger.ui.theme.sub2

@Composable
fun RoundButton(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit
){
    val buttonShape = RoundedCornerShape(25.dp)

    MessengerTheme {
        Button(
            onClick = onClick,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(
                horizontal = 10.dp,
                vertical = 17.dp
            ),
            modifier = modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.secondary,
                    shape = buttonShape
                ),

            ) {
            Text(
                text = title,
                style = sub2,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }

}