package com.example.careconnect.ui.employee.employeeProfile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import com.example.careconnect.R
import com.example.careconnect.domain.model.EmployeeDocument
import com.example.careconnect.mock.MockData
import com.example.careconnect.mock.repository.EmployeeRepositoryMock
import com.example.careconnect.ui.components.CareTopBar
import com.example.careconnect.ui.components.LoadingView
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.color
import com.example.careconnect.util.FileHelper.fileName
import com.example.careconnect.util.FileHelper.readBytes
import com.example.careconnect.util.UriHelper.openSafely
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun EmployeeProfileScreen(
    onBack: () -> Unit,
    viewModel: EmployeeProfileViewModel = koinViewModel()
) {
    val employee by viewModel.employee.collectAsState()
    val documents by viewModel.documents.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var documentToDelete by remember { mutableStateOf<EmployeeDocument?>(null) }

    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // PDF über den Datei-Dialog von Android auswählen
    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val bytes = context.readBytes(uri) ?: return@launch
                viewModel.uploadPdf(context.fileName(uri), bytes)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CareTopBar(
            title = stringResource(R.string.mitarbeiter),
            onBack = onBack,
            actions = {
                IconButton(onClick = { pdfLauncher.launch(arrayOf("application/pdf")) }) {
                    Icon(Icons.Default.UploadFile, contentDescription = stringResource(R.string.dokument_hinzufuegen))
                }
            }
        )

        val current = employee
        if (current == null) {
            LoadingView(stringResource(R.string.lade))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileHeader(current)
                WorkScheduleCard(current)

                SectionCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.dokumente),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        if (isUploading) {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        } else {
                            Button(onClick = { pdfLauncher.launch(arrayOf("application/pdf")) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text(stringResource(R.string.hinzufuegen))
                            }
                        }
                    }

                    if (documents.isEmpty()) {
                        Text(
                            stringResource(R.string.noch_keine_dokumente_vorhanden),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        documents.forEach { document ->
                            DocumentRow(
                                document = document,
                                onOpen = { uriHandler.openSafely(document.downloadUrl) },
                                onDelete = { documentToDelete = document }
                            )
                        }
                    }
                }
            }
        }
    }

    documentToDelete?.let { document ->
        AlertDialog(
            onDismissRequest = { documentToDelete = null },
            title = { Text(stringResource(R.string.dokument_loeschen)) },
            text = { Text(document.fileName) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteDocument(document)
                    documentToDelete = null
                }) { Text(stringResource(R.string.loeschen), color = StatusRed) }
            },
            dismissButton = {
                TextButton(onClick = { documentToDelete = null }) { Text(stringResource(R.string.abbrechen)) }
            }
        )
    }

    errorMessage?.let { message ->
        AlertDialog(
            onDismissRequest = viewModel::dismissError,
            title = { Text(stringResource(R.string.fehler)) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = viewModel::dismissError) { Text("OK") } }
        )
    }
}

@Preview
@Composable
fun EmployeeProfileScreenPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeProfileScreen(
            onBack = {},
            viewModel = EmployeeProfileViewModel(
                EmployeeRepositoryMock(),
                SavedStateHandle(mapOf("employeeId" to MockData.employee.id))
            )
        )
    }
}
