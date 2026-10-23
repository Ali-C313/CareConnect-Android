package com.example.careconnect.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.StorageRepositoryMock
import com.example.careconnect.mock.repository.UserRepositoryMock
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.DateTimePickerField
import com.example.careconnect.ui.components.ProfileImagePicker
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val imageBytes by viewModel.imageBytes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(title = stringResource(R.string.registrieren), onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.erstelle_careconnect_konto),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            SectionCard {
                ProfileImagePicker(
                    imageBytes = imageBytes,
                    imageUrl = "",
                    onImagePicked = viewModel::onImagePicked
                )
            }

            // Persönliche Daten
            SectionCard(title = stringResource(R.string.persoenliche_daten), icon = Icons.Default.Person) {
                RegisterTextField(stringResource(R.string.vorname), uiState.firstName, viewModel::onFirstNameChange, Icons.Default.Person)
                RegisterTextField(stringResource(R.string.nachname), uiState.lastName, viewModel::onLastNameChange, Icons.Default.Person)

                DateTimePickerField(
                    label = stringResource(R.string.geburtsdatum),
                    millis = uiState.birthDate,
                    onValueChange = viewModel::onBirthDateChange,
                    withTime = false
                )
            }

            // Adresse brauchen vor allem Patienten (Hausbesuche, Karte)
            SectionCard(title = stringResource(R.string.adresse_kontakt), icon = Icons.Default.Apartment) {
                AddressSearchField(
                    value = uiState.address,
                    onValueChange = viewModel::onAddressChange,
                    suggestions = uiState.addressSuggestions,
                    onSuggestionClick = viewModel::onSuggestionClick
                )
                RegisterTextField(stringResource(R.string.stadt), uiState.city, viewModel::onCityChange, Icons.Default.Apartment)
                RegisterTextField(
                    stringResource(R.string.telefonnummer),
                    uiState.phone,
                    viewModel::onPhoneChange,
                    Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )
            }

            // Zugangsdaten
            SectionCard(title = stringResource(R.string.zugangsdaten), icon = Icons.Default.Lock) {
                RegisterTextField(
                    stringResource(R.string.e_mail_adresse),
                    uiState.email,
                    viewModel::onEmailChange,
                    Icons.Default.Email,
                    keyboardType = KeyboardType.Email
                )
                RegisterTextField(
                    stringResource(R.string.passwort),
                    uiState.password,
                    viewModel::onPasswordChange,
                    Icons.Default.Lock,
                    isPassword = true
                )
                RegisterTextField(
                    stringResource(R.string.passwort_bestaetigen),
                    uiState.confirmPassword,
                    viewModel::onConfirmPasswordChange,
                    Icons.Default.Lock,
                    isPassword = true
                )
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = viewModel::register,
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.konto_erstellen), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Preview
@Composable
fun RegisterScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        RegisterScreen(
            onBack = {},
            viewModel = RegisterViewModel(
                AuthRepositoryMock(),
                UserRepositoryMock(),
                StorageRepositoryMock(),
                GeoRepositoryMock()
            )
        )
    }
}
