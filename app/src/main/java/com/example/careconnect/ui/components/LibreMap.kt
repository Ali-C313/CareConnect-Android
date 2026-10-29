package com.example.careconnect.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.expressions.value.LineCap
import org.maplibre.compose.expressions.value.LineJoin
import org.maplibre.compose.layers.CircleLayer
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.MaplibreComposable
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position

private const val STYLE_LIGHT = "https://tiles.openfreemap.org/styles/liberty"
private const val STYLE_DARK = "https://tiles.openfreemap.org/styles/dark"

private val routeBlue = Color(0xFF2196F3)
private val targetRed = Color(0xFFE53935)

/** MapLibre-Karte mit Markern, route wird als blaue Linie eingezeichnet */
@Composable
fun LibreMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    route: List<Coordinate> = emptyList()
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val start = markers.firstOrNull()?.coordinate ?: Coordinate(52.5200, 13.4050)

    val cameraState = rememberCameraState(
        CameraPosition(
            target = start.toPosition(),
            zoom = 14.0
        )
    )

    // Nur neu zoomen, wenn Marker dazukommen/wegfallen oder die Route zum ersten Mal da ist
    val fitKey = markers.map { it.title } + if (route.size >= 2) listOf("route") else emptyList()
    LaunchedEffect(fitKey) {
        val points = markers.map { it.coordinate } + route
        when {
            points.isEmpty() -> Unit
            points.size == 1 -> cameraState.animateTo(
                CameraPosition(target = points.first().toPosition(), zoom = 15.0)
            )
            else -> cameraState.animateTo(
                boundingBox = points.toBoundingBox(),
                padding = PaddingValues(48.dp)
            )
        }
    }

    MaplibreMap(
        modifier = modifier,
        baseStyle = BaseStyle.Uri(if (isDark) STYLE_DARK else STYLE_LIGHT),
        cameraState = cameraState
    ) {
        if (route.size >= 2) {
            RouteLayer(route)
        }
        markers.forEach { marker ->
            key(marker.title) {
                MarkerLayer(marker)
            }
        }
    }
}

// Strecke wie bei Lieferdiensten: blaue Linie vom Mitarbeiter zum Ziel
@Composable
@MaplibreComposable
private fun RouteLayer(route: List<Coordinate>) {
    val routeSource = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            LineString(route.map { it.toPosition() })
        )
    )

    LineLayer(
        id = "route",
        source = routeSource,
        color = const(routeBlue),
        width = const(6.dp),
        cap = const(LineCap.Round),
        join = const(LineJoin.Round)
    )
}

// Auto für den Mitarbeiter, roter Punkt für das Ziel
@Composable
@MaplibreComposable
private fun MarkerLayer(marker: MapMarker) {
    // Auto gleitet zur neuen Position statt zu springen
    val latitude by animateFloatAsState(
        targetValue = marker.coordinate.latitude.toFloat(),
        animationSpec = tween(durationMillis = 1_500, easing = LinearEasing)
    )
    val longitude by animateFloatAsState(
        targetValue = marker.coordinate.longitude.toFloat(),
        animationSpec = tween(durationMillis = 1_500, easing = LinearEasing)
    )

    val source = rememberGeoJsonSource(
        data = GeoJsonData.Features(
            Point(Position(longitude = longitude.toDouble(), latitude = latitude.toDouble()))
        )
    )

    if (marker.isEmployee) {
        SymbolLayer(
            id = "marker-${marker.title}",
            source = source,
            iconImage = image(painterResource(R.drawable.ic_helper_car)),
            iconAllowOverlap = const(true)
        )
    } else {
        CircleLayer(
            id = "marker-${marker.title}",
            source = source,
            color = const(targetRed),
            radius = const(9.dp),
            strokeColor = const(Color.White),
            strokeWidth = const(3.dp)
        )
    }
}

private fun Coordinate.toPosition() = Position(longitude = longitude, latitude = latitude)

private fun List<Coordinate>.toBoundingBox() = BoundingBox(
    west = minOf { it.longitude },
    south = minOf { it.latitude },
    east = maxOf { it.longitude },
    north = maxOf { it.latitude }
)
