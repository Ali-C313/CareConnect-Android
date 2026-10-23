package com.example.careconnect.ui.employee.orderDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusBlue
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.ui.theme.color
import com.example.careconnect.ui.theme.icon

/**
 * Aktionen passend zum Status: annehmen, abschließen, bearbeiten, löschen.
 */
@Composable
fun ActionCard(
    state: OrderState,
    isMine: Boolean,
    assignedName: String?,
    onEdit: () -> Unit,
    onAccept: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    // Angenommene Aufträge darf nur der zugewiesene Mitarbeiter ändern
    val canModify = state == OrderState.OFFEN || isMine

    SectionCard(title = stringResource(R.string.aktionen), icon = Icons.Default.Edit) {
        if (canModify && state != OrderState.ERLEDIGT) {
            ActionButton(stringResource(R.string.bearbeiten), Icons.Default.Edit, MaterialTheme.colorScheme.primary, onEdit)
        }

        when (state) {
            OrderState.OFFEN -> ActionButton(stringResource(R.string.annehmen), Icons.Default.CheckCircle, StatusBlue, onAccept)
            OrderState.AKTUELL -> if (isMine) {
                ActionButton(stringResource(R.string.abschliessen), Icons.Default.Verified, StatusGreen, onComplete)
            } else {
                Text(
                    text = stringResource(R.string.angenommen_von, assignedName ?: stringResource(R.string.einem_kollegen)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            OrderState.ERLEDIGT -> Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = StatusGreen)
                Text(
                    stringResource(R.string.auftrag_abgeschlossen),
                    color = StatusGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (canModify) {
            ActionButton(stringResource(R.string.loeschen), Icons.Default.Delete, StatusRed, onDelete)
        }
    }
}

@Preview
@Composable
fun ActionCardPreview() {
    CareConnectTheme(darkTheme = false) {
        ActionCard(
            state = OrderState.OFFEN,
            isMine = false,
            assignedName = null,
            onEdit = {},
            onAccept = {},
            onComplete = {},
            onDelete = {}
        )
    }
}
