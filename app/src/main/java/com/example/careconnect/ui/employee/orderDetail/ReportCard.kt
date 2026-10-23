package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.CompletionReport
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.components.SignatureView
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.toDateTimeString

/**
 * Zeigt den Leistungsnachweis eines erledigten Auftrags.
 */
@Composable
fun ReportCard(report: CompletionReport) {
    SectionCard(title = stringResource(R.string.leistungsnachweis), icon = Icons.AutoMirrored.Filled.Notes) {
        Text(report.note)
        val info = listOfNotNull(
            report.completedByName.ifBlank { null },
            report.completedAt?.toDateTimeString()
        ).joinToString(" · ")
        if (info.isNotBlank()) {
            Text(info, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (report.signature.isNotBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(Icons.Default.Draw, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.unterschrieben, report.signedBy.ifBlank { stringResource(R.string.patient) }), style = MaterialTheme.typography.labelLarge)
            }
            SignatureView(signature = report.signature)
        } else {
            Text(stringResource(R.string.ohne_unterschrift), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview
@Composable
fun ReportCardPreview() {
    CareConnectTheme(darkTheme = false) {
        ReportCard(report = MockData.report)
    }
}
