package com.example.careconnect.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Zeigt eine gespeicherte Unterschrift an.
 */
@Composable
fun SignatureView(
    signature: String,
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    val strokes = remember(signature) { decodeSignature(signature) }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        strokes.forEach { drawSignatureStroke(it) }
    }
}

@Preview
@Composable
fun SignatureViewPreview() {
    CareConnectTheme(darkTheme = false) {
        SignatureView(signature = "0.1,0.5;0.3,0.3;0.5,0.6;0.7,0.4;0.9,0.5")
    }
}
