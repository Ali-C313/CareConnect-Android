package com.example.careconnect.ui.patient.orders

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.careconnect.R
import com.example.careconnect.domain.model.Order
import com.example.careconnect.domain.model.OrderState
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.components.SectionCard
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.DateHelper.toDateTimeString

// Erstellt -> Angenommen -> Erledigt
@Composable
fun StatusTimeline(order: Order) {
    SectionCard(title = stringResource(R.string.status), icon = Icons.Default.Timeline) {
        TimelineStep(stringResource(R.string.erstellt), order.createdAt?.toDateTimeString(), done = true)
        TimelineStep(stringResource(R.string.angenommen), null, done = order.state != OrderState.OFFEN)
        TimelineStep(
            stringResource(R.string.erledigt),
            order.report?.completedAt?.toDateTimeString(),
            done = order.state == OrderState.ERLEDIGT
        )
    }
}

@Preview
@Composable
fun StatusTimelinePreview() {
    CareConnectTheme(darkTheme = false) {
        StatusTimeline(order = MockData.orders.last())
    }
}
