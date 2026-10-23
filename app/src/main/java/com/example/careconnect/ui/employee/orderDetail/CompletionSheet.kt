package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.CareTextField
import com.example.careconnect.ui.components.SignaturePad
import com.example.careconnect.ui.components.SignatureStrokes
import com.example.careconnect.ui.components.encodeSignature
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Abschließen mit Leistungsnachweis: was wurde gemacht + Unterschrift des Patienten.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletionSheet(
    patientName: String,
    onConfirm: (note: String, signature: String, signedBy: String) -> Unit,
    onDismiss: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    var signedBy by remember { mutableStateOf(patientName) }
    var strokes by remember { mutableStateOf<SignatureStrokes>(emptyList()) }
    var noSignature by remember { mutableStateOf(false) }
    val canSave = note.isNotBlank() && (noSignature || strokes.isNotEmpty())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(stringResource(R.string.auftrag_abschliessen), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.was_wurde_gemacht)) },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Text(stringResource(R.string.unterschrift), style = MaterialTheme.typography.labelLarge)
            if (!noSignature) {
                CareTextField(label = stringResource(R.string.name), value = signedBy, onValueChange = { signedBy = it })
                SignaturePad(strokes = strokes, onStrokesChange = { strokes = it })
                TextButton(onClick = { strokes = emptyList() }) { Text(stringResource(R.string.loeschen)) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = noSignature, onCheckedChange = { noSignature = it })
                Text(stringResource(R.string.unterschrift_nicht_moeglich))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text(stringResource(R.string.abbrechen)) }
                Button(
                    onClick = {
                        onConfirm(
                            note,
                            if (noSignature) "" else encodeSignature(strokes),
                            if (noSignature) "" else signedBy
                        )
                    },
                    enabled = canSave,
                    modifier = Modifier.weight(1f)
                ) { Text(stringResource(R.string.abschliessen)) }
            }
        }
    }
}

@Preview
@Composable
fun CompletionSheetPreview() {
    CareConnectTheme(darkTheme = false) {
        CompletionSheet(
            patientName = MockData.patient.name,
            onConfirm = { _, _, _ -> },
            onDismiss = {}
        )
    }
}
