package com.example.careconnect.ui.patient.orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoButtonRow
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.UriHelper.openSafely

@Composable
fun EmployeeSection(employee: Employee?) {
    val uriHandler = LocalUriHandler.current
    SectionCard(title = stringResource(R.string.mitarbeiter), icon = Icons.Default.Person) {
        InfoRow(Icons.Default.Person, stringResource(R.string.mitarbeiter), employee?.name?.ifBlank { null } ?: stringResource(R.string.unbekannt))
        // In Firestore gibt es keine Telefonnummer, deshalb E-Mail als Kontakt
        employee?.email?.takeIf { it.isNotEmpty() }?.let { email ->
            InfoButtonRow(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.kontakt),
                value = email,
                actionIcon = Icons.Default.Call,
                actionDescription = stringResource(R.string.kontaktieren),
                onClick = { uriHandler.openSafely("mailto:$email") }
            )
        }
    }
}

@Preview
@Composable
fun EmployeeSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        EmployeeSection(employee = MockData.employee)
    }
}
