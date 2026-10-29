package com.example.careconnect.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// Striche mit Werten von 0 bis 1, damit die Unterschrift in jeder Größe passt
typealias SignatureStrokes = List<List<Offset>>

// Unterschrift zeichnen und als Text speichern
object SignatureHelper {

    fun DrawScope.drawSignatureStroke(points: List<Offset>) {
        if (points.size < 2) return
        val path = Path().apply {
            moveTo(points.first().x * size.width, points.first().y * size.height)
            points.drop(1).forEach { lineTo(it.x * size.width, it.y * size.height) }
        }
        drawPath(
            path = path,
            color = Color(0xFF1C1C1E),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
    /** @return z.B. "120,450;130,460|..." mit Werten von 0 bis 1000 */
    fun encodeSignature(strokes: SignatureStrokes): String =
        strokes.filter { it.size > 1 }.joinToString("|") { stroke ->
            stroke.joinToString(";") { "${(it.x * 1000).roundToInt()},${(it.y * 1000).roundToInt()}" }
        }
    fun decodeSignature(signature: String): SignatureStrokes =
        if (signature.isBlank()) emptyList()
        else signature.split("|").map { stroke ->
            stroke.split(";").mapNotNull { point ->
                val parts = point.split(",")
                val x = parts.getOrNull(0)?.toFloatOrNull() ?: return@mapNotNull null
                val y = parts.getOrNull(1)?.toFloatOrNull() ?: return@mapNotNull null
                Offset(x / 1000f, y / 1000f)
            }
        }
}
