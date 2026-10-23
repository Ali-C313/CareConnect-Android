package com.example.careconnect.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

@Composable
fun ChangePasswordCard(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onChangePassword: () -> Unit
) {
    SectionCard(title = stringResource(R.string.passwort_aendern), icon = Icons.Default.Lock) {
        CareTextField(stringResource(R.string.aktuelles_passwort), currentPassword, onCurrentPasswordChange, isPassword = true)
        CareTextField(stringResource(R.string.neues_passwort), newPassword, onNewPasswordChange, isPassword = true)
        CareTextField(stringResource(R.string.neues_passwort_bestaetigen), confirmPassword, onConfirmPasswordChange, isPassword = true)

        Button(
            onClick = onChangePassword,
            enabled = !isLoading && newPassword.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Icon(Icons.Default.Key, contentDescription = null)
                Text(stringResource(R.string.passwort_aendern_button))
            }
        }
    }
}

@Preview
@Composable
fun ChangePasswordCardPreview() {
    CareConnectTheme(darkTheme = false) {
        ChangePasswordCard(
            currentPassword = "",
            newPassword = "",
            confirmPassword = "",
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            isLoading = false,
            onChangePassword = {}
        )
    }
}
