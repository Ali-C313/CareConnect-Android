package com.example.careconnect.ui.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.theme.CareConnectTheme

/** Textfeld für die Registrierung */
@Composable
fun RegisterTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    CareTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        icon = icon,
        keyboardType = keyboardType,
        isPassword = isPassword
    )
}

@Preview
@Composable
fun RegisterTextFieldPreview() {
    CareConnectTheme(darkTheme = false) {
        RegisterTextField(
            label = "Vorname",
            value = "Ali",
            onValueChange = {},
            icon = Icons.Default.Person
        )
    }
}
