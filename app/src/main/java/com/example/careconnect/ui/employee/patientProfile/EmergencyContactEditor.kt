package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AppointmentRepositoryMock
import com.example.careconnect.mock.repository.GeoRepositoryMock
import com.example.careconnect.mock.repository.MedicineRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.AddressSearchField
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Formular für den Notfallkontakt (entspricht EmergencyContactEditorView).
 */
@Composable
fun EmergencyContactEditor(
    viewModel: PatientProfileViewModel,
    onDismiss: () -> Unit
) {
    val contact by viewModel.editContact.collectAsState()
    val suggestions by viewModel.addressSuggestions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.notfallkontakt_hinzufuegen),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        ContactTextField(stringResource(R.string.name), contact.name) {
            viewModel.onContactChange(contact.copy(name = it))
        }
        ContactTextField(stringResource(R.string.beziehung_beispiel), contact.relation) {
            viewModel.onContactChange(contact.copy(relation = it))
        }
        ContactTextField(stringResource(R.string.alter), contact.age?.toString() ?: "", KeyboardType.Number) {
            viewModel.onContactChange(contact.copy(age = it.filter { c -> c.isDigit() }.toIntOrNull()))
        }
        ContactTextField(stringResource(R.string.telefonnummer), contact.phone, KeyboardType.Phone) {
            viewModel.onContactChange(contact.copy(phone = it))
        }
        ContactTextField(stringResource(R.string.e_mail_adresse), contact.email, KeyboardType.Email) {
            viewModel.onContactChange(contact.copy(email = it))
        }
        AddressSearchField(
            value = contact.address,
            onValueChange = viewModel::onContactAddressChange,
            suggestions = suggestions,
            onSuggestionClick = viewModel::onContactSuggestionClick
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.abbrechen))
            }
            Button(
                onClick = { viewModel.saveEmergencyContact(onDone = onDismiss) },
                enabled = contact.name.isNotBlank(),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.speichern))
            }
        }
    }
}

@Preview
@Composable
fun EmergencyContactEditorPreview() {
    CareConnectTheme(darkTheme = false) {
        EmergencyContactEditor(
            viewModel = PatientProfileViewModel(
                PatientRepositoryMock(),
                GeoRepositoryMock(),
                MedicineRepositoryMock(),
                AppointmentRepositoryMock(),
                SavedStateHandle(mapOf("patientId" to MockData.patient.id))
            ),
            onDismiss = {}
        )
    }
}
