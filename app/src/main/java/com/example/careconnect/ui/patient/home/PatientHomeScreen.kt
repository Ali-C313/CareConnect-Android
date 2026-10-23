package com.example.careconnect.ui.patient.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.AuthRepositoryMock
import com.example.careconnect.mock.repository.OrderRepositoryMock
import com.example.careconnect.mock.repository.PatientRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.EmergencyButton
import com.example.careconnect.ui.theme.Accent
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.StringHelper
import com.example.careconnect.util.UriHelper.openSafely
import org.koin.androidx.compose.koinViewModel

@Composable
fun PatientHomeScreen(
    onOrdersClick: () -> Unit,
    viewModel: PatientHomeViewModel = koinViewModel()
) {
    val patient by viewModel.patient.collectAsState()
    val message by viewModel.message.collectAsState()
    var showEmergencyDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Accent.copy(alpha = 0.2f), MaterialTheme.colorScheme.background)
                )
            )
    ) {
        CareTopBar(title = stringResource(R.string.home))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(
                Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = stringResource(R.string.slogan),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.willkommen_text),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            EmergencyButton(onClick = { showEmergencyDialog = true })

            ElevatedButton(
                onClick = onOrdersClick,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Text(stringResource(R.string.zu_meinen_auftraegen), fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "© 2025 CareConnect",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showEmergencyDialog) {
        val contactPhone = patient?.emergencyContact?.phone?.ifBlank { null }

        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = { Text(stringResource(R.string.notfallaktion)) },
            text = {
                Column {
                    Text(stringResource(R.string.notfallaktion_waehlen))
                    // 112 steht immer ganz oben
                    TextButton(onClick = {
                        showEmergencyDialog = false
                        uriHandler.openSafely("tel:112")
                    }) {
                        Text(stringResource(R.string.notruf_112_anrufen), color = StatusRed, fontWeight = FontWeight.Bold)
                    }
                    contactPhone?.let { phone ->
                        TextButton(onClick = {
                            showEmergencyDialog = false
                            uriHandler.openSafely("tel:${StringHelper.formattedPhone(phone)}")
                        }) { Text(stringResource(R.string.notfallkontakt_anrufen)) }
                    }
                    TextButton(onClick = {
                        showEmergencyDialog = false
                        viewModel.createEmergencyOrder()
                    }) { Text(stringResource(R.string.pflegedienst_rufen_dringender_auftrag)) }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showEmergencyDialog = false }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text(stringResource(R.string.notfall)) },
            text = { Text(text) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun PatientHomeScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        PatientHomeScreen(
            onOrdersClick = {},
            viewModel = PatientHomeViewModel(
                OrderRepositoryMock(),
                AuthRepositoryMock(MockData.patient.id),
                PatientRepositoryMock()
            )
        )
    }
}
