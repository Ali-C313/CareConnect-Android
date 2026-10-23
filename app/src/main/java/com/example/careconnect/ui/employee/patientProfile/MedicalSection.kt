package com.example.careconnect.ui.employee.patientProfile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Appointment
import com.example.careconnect.domain.model.MedicineEntry
import com.example.careconnect.domain.model.Patient
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusPurple
import com.example.careconnect.ui.theme.StatusRed
import com.example.careconnect.util.DateHelper.toAge
import com.example.careconnect.util.DateHelper.toDateString
import com.example.careconnect.util.DateHelper.toDateTimeString

@Composable
fun MedicalSection(
    patient: Patient,
    medicines: List<MedicineEntry>,
    appointments: List<Appointment>
) {
    if (medicines.isNotEmpty()) {
        SectionCard(title = stringResource(R.string.medikamente), icon = Icons.Default.Medication, iconTint = StatusPurple) {
            medicines.forEach { med ->
                Text("• ${med.name} – ${med.dosage} (${med.timeOfDay.displayName})")
            }
        }
    } else if (patient.medications.isNotEmpty()) {
        // Alte Einträge aus der iOS-App
        SectionCard(title = stringResource(R.string.medikamente), icon = Icons.Default.Medication, iconTint = StatusPurple) {
            patient.medications.forEach { med ->
                Text("• ${med.name}")
            }
        }
    }

    if (appointments.isNotEmpty()) {
        SectionCard(title = stringResource(R.string.naechste_termine), icon = Icons.Default.CalendarMonth) {
            appointments.take(3).forEach { appointment ->
                Text("• ${appointment.date.toDateTimeString()} – ${appointment.what}")
            }
        }
    }

    if (patient.allergies.isNotBlank()) {
        SectionCard(title = stringResource(R.string.allergien), icon = Icons.Default.Warning, iconTint = StatusOrange) {
            Text(patient.allergies)
        }
    }

    if (patient.medicalHistory.isNotBlank()) {
        SectionCard(title = stringResource(R.string.medizinische_vorgeschichte), icon = Icons.Default.Favorite, iconTint = StatusRed) {
            Text(patient.medicalHistory)
        }
    }

    if (patient.history.isNotEmpty()) {
        SectionCard(title = stringResource(R.string.historie), icon = Icons.Default.History) {
            patient.history.forEach { entry ->
                Text("• $entry")
            }
        }
    }

    patient.birthDate?.let { birthDate ->
        SectionCard(title = stringResource(R.string.geburtsdatum), icon = Icons.Default.Cake) {
            InfoRow(Icons.Default.Cake, stringResource(R.string.geboren_am), birthDate.toDateString())
            InfoRow(Icons.Default.Cake, stringResource(R.string.alter), stringResource(R.string.jahre, birthDate.toAge()))
        }
    }

    if (patient.notes.isNotBlank()) {
        SectionCard(title = stringResource(R.string.notizen), icon = Icons.AutoMirrored.Filled.Notes) {
            Text(patient.notes)
        }
    }
}

@Preview
@Composable
fun MedicalSectionPreview() {
    CareConnectTheme(darkTheme = false) {
        MedicalSection(
            patient = MockData.patient,
            medicines = MockData.medicines,
            appointments = MockData.appointments
        )
    }
}
