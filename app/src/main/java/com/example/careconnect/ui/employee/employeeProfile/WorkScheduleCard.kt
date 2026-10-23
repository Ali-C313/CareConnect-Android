package com.example.careconnect.ui.employee.employeeProfile

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Employee
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.InfoRow
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.ResourceHelper

@Composable
fun WorkScheduleCard(employee: Employee) {
    val schedule = employee.workSchedule
    SectionCard(title = stringResource(R.string.arbeitszeiten), icon = Icons.Default.Schedule) {
        InfoRow(Icons.Default.Schedule, stringResource(R.string.wochenstunden), stringResource(R.string.std_woche, schedule.weeklyHours))
        InfoRow(Icons.Default.Schedule, stringResource(R.string.schicht), "${schedule.shiftStart} – ${schedule.shiftEnd}")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            schedule.workDays.forEach { day ->
                Text(
                    text = ResourceHelper.translate(day),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun WorkScheduleCardPreview() {
    CareConnectTheme(darkTheme = false) {
        WorkScheduleCard(employee = MockData.employee)
    }
}
