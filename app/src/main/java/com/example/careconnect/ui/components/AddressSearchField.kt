package com.example.careconnect.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.AddressSuggestion
import com.example.careconnect.mock.MockData
import com.example.careconnect.ui.theme.CareConnectTheme
import com.example.careconnect.util.ResourceHelper

/**
 * Textfeld mit Adressvorschlägen darunter (Vorschläge kommen aus dem ViewModel).
 */
@Composable
fun AddressSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestions: List<AddressSuggestion>,
    onSuggestionClick: (AddressSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ResourceHelper.getString(R.string.adresse)
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (suggestions.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                suggestions.forEachIndexed { index, suggestion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSuggestionClick(suggestion) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = suggestion.formatted,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (index < suggestions.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AddressSearchFieldPreview() {
    CareConnectTheme(darkTheme = false) {
        AddressSearchField(
            value = "Torstr",
            onValueChange = {},
            suggestions = listOf(AddressSuggestion("Torstraße 12, 10119 Berlin", MockData.patientLocation)),
            onSuggestionClick = {}
        )
    }
}
