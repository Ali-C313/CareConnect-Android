package com.example.careconnect.ui.employee.prescriptions

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.domain.model.PrescriptionStatus
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.ui.theme.StatusBlue
import com.example.careconnect.ui.theme.StatusGreen
import com.example.careconnect.ui.theme.StatusOrange
import com.example.careconnect.ui.theme.StatusRed

@Composable
fun StatusLabel(status: PrescriptionStatus) {
    val color: Color = when (status) {
        PrescriptionStatus.OFFEN -> StatusOrange
        PrescriptionStatus.ANGENOMMEN -> StatusBlue
        PrescriptionStatus.ABGELEHNT -> StatusRed
        PrescriptionStatus.ERLEDIGT -> StatusGreen
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(8.dp)) {
        Text(
            status.displayName,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview
@Composable
fun StatusLabelPreview() {
    CareConnectTheme(darkTheme = false) {
        StatusLabel(status = PrescriptionStatus.OFFEN)
    }
}
