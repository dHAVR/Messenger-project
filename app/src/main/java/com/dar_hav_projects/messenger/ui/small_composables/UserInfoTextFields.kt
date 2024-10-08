package com.dar_hav_projects.messenger.ui.small_composables

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dar_hav_projects.messenger.ui.theme.body1
import com.dar_hav_projects.messenger.ui.theme.meta1

@Composable
fun UserInfoTextFields(
    modifier: Modifier = Modifier,
    placeholder: String,
    value: String,
    isNotValid: Boolean,
    showValidation: Boolean = true,
    imeAction: ImeAction,
    onValueChanged: (String) -> Unit
){

    Column(
        modifier = modifier,

        ) {
        TextField(
            value = value,
            onValueChange = {
                onValueChanged(it)
            },
            placeholder = { Text(text = placeholder) },
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
            isError = showValidation && isNotValid,
            textStyle = body1.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(
                    2.dp,
                    color = if (showValidation && isNotValid) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp)
        )

        if (showValidation &&  isNotValid){
            Text(
                text = "*Text field should not be empty",
                style = meta1.copy(
                    color = MaterialTheme.colorScheme.error
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(top = 8.dp)
            )
        }
    }

}