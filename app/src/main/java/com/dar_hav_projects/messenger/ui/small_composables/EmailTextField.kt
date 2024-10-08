package com.dar_hav_projects.messenger.ui.small_composables

import com.dar_hav_projects.messenger.R
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.body2
import com.dar_hav_projects.messenger.ui.theme.meta1
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.ui.theme.MessengerTheme
import com.dar_hav_projects.messenger.ui.theme.sub1
import com.dar_hav_projects.messenger.ui.theme.sub2


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    title: String,
    email: String,
    isValid: Boolean,
    showValidation: Boolean = true,
    onEmailChanged: (String) -> Unit
){

    MessengerTheme {
        Column(
            modifier = modifier,

            ) {
            Text(
                text = title,
                style = sub2,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextField(
                value = email,
                onValueChange = onEmailChanged,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    errorContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                ),
                isError = showValidation && isValid,
                textStyle = body1.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onDone = KeyboardActions.Default.onNext),
                trailingIcon = {
                    if (showValidation){
                        if (isValid){
                            Icon(
                                painter = painterResource(id = R.drawable.check),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.icon_size))
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.icon_size))
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(
                        2.dp,
                        color = if (showValidation && !isValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp)

            )
            if (showValidation && !isValid){
                Text(
                    text = "*Not valid email",
                    style = meta1.copy(
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
