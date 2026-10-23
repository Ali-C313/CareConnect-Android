package com.example.careconnect.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.ui.theme.CareConnectTheme

/**
 * Feld zum Unterschreiben mit dem Finger.
 */
@Composable
fun SignaturePad(
    strokes: SignatureStrokes,
    onStrokesChange: (SignatureStrokes) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 180.dp
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    var current by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val latestStrokes by rememberUpdatedState(strokes)
    val latestOnChange by rememberUpdatedState(onStrokesChange)

    fun normalize(point: Offset): Offset =
        if (size.width == 0 || size.height == 0) Offset.Zero
        else Offset(
            (point.x / size.width).coerceIn(0f, 1f),
            (point.y / size.height).coerceIn(0f, 1f)
        )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (strokes.isEmpty() && current.isEmpty()) {
            Text(stringResource(R.string.hier_unterschreiben), color = Color.Gray)
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size = it }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { start -> current = listOf(normalize(start)) },
                        onDrag = { change, _ ->
                            change.consume()
                            current = current + normalize(change.position)
                        },
                        onDragEnd = {
                            if (current.size > 1) latestOnChange(latestStrokes + listOf(current))
                            current = emptyList()
                        },
                        onDragCancel = { current = emptyList() }
                    )
                }
        ) {
            (latestStrokes + listOf(current)).forEach { drawSignatureStroke(it) }
        }
    }
}

@Preview
@Composable
fun SignaturePadPreview() {
    CareConnectTheme(darkTheme = false) {
        SignaturePad(strokes = emptyList(), onStrokesChange = {})
    }
}
