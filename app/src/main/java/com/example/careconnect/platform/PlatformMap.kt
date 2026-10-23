package com.example.careconnect.platform

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.viewinterop.AndroidView
import com.example.careconnect.R
import com.example.careconnect.domain.model.Coordinate
import com.example.careconnect.domain.model.MapMarker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File

// Esri-Straßenkarte, braucht keinen Key (Esri erwartet z/y/x statt z/x/y)
private object EsriStreetTiles : OnlineTileSourceBase(
    "Esri-WorldStreetMap",
    0,
    19,
    256,
    "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"),
    "Kartendaten © Esri, HERE, Garmin, OpenStreetMap-Mitwirkende"
) {
    override fun getTileURLString(pMapTileIndex: Long): String =
        baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" +
            MapTileIndex.getY(pMapTileIndex) + "/" +
            MapTileIndex.getX(pMapTileIndex)
}

// Farben umdrehen für den Dunkelmodus
private val invertedColors = ColorMatrixColorFilter(
    ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, 255f,
            0f, -1f, 0f, 0f, 255f,
            0f, 0f, -1f, 0f, 255f,
            0f, 0f, 0f, 1f, 0f
        )
    )
)

private val routeBlue = Color.rgb(33, 150, 243)
private val targetRed = Color.rgb(229, 57, 53)

// Marker und Linie bleiben erhalten, damit sich das Auto flüssig bewegen kann
private class MapState {
    val markers = mutableMapOf<String, Marker>()
    val animations = mutableMapOf<String, ValueAnimator>()
    var line: Polyline? = null
    var lastFit: List<String> = emptyList()
}

// OpenStreetMap-Karte, route wird als Linie eingezeichnet
@Composable
fun PlatformMap(
    markers: List<MapMarker>,
    modifier: Modifier = Modifier,
    route: List<Coordinate> = emptyList()
) {
    val state = remember { MapState() }
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    AndroidView(
        modifier = modifier,
        factory = { context ->
            Configuration.getInstance().apply {
                userAgentValue = "CareConnect/1.0 (Android)"
                osmdroidBasePath = File(context.cacheDir, "osmdroid")
                osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
            }
            MapView(context).apply {
                setTileSource(EsriStreetTiles)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                controller.setZoom(14.0)
                overlays.add(CopyrightOverlay(context))
                // Karte verschieben, ohne dass die Seite mitscrollt
                setOnTouchListener { view, event ->
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                    if (event.action == MotionEvent.ACTION_UP) view.performClick()
                    false
                }
            }
        },
        update = { map ->
            // Im Dunkelmodus Farben umdrehen
            map.overlayManager.tilesOverlay.setColorFilter(if (isDark) invertedColors else null)

            updateRoute(map, state, route)
            updateMarkers(map, state, markers)
            map.invalidate()

            // Nur neu zoomen, wenn Marker dazukommen/wegfallen oder die Route zum ersten Mal da ist
            val fitKey = markers.map { it.title } + if (route.size >= 2) listOf("route") else emptyList()
            if (fitKey != state.lastFit && markers.isNotEmpty()) {
                state.lastFit = fitKey
                val points = markers.map { it.coordinate.toGeoPoint() } + route.map { it.toGeoPoint() }
                map.post {
                    if (points.size == 1) {
                        map.controller.setZoom(15.0)
                        map.controller.setCenter(points.first())
                    } else {
                        runCatching {
                            map.zoomToBoundingBox(BoundingBox.fromGeoPoints(points).increaseByScale(1.3f), false)
                        }
                    }
                }
            }
        },
        onRelease = { map ->
            state.animations.values.forEach { it.cancel() }
            map.onDetach()
        }
    )
}

// Strecke wie bei Lieferdiensten: blaue Linie vom Mitarbeiter zum Ziel
private fun updateRoute(map: MapView, state: MapState, route: List<Coordinate>) {
    val line = state.line ?: Polyline(map).also { newLine ->
        newLine.outlinePaint.apply {
            color = routeBlue
            strokeWidth = 6f * map.resources.displayMetrics.density
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
        }
        newLine.infoWindow = null
        // Unter die Marker legen
        map.overlays.add(0, newLine)
        state.line = newLine
    }
    line.setPoints(if (route.size >= 2) route.map { it.toGeoPoint() } else emptyList())
}

private fun updateMarkers(map: MapView, state: MapState, markers: List<MapMarker>) {
    val titles = markers.map { it.title }.toSet()

    // Weggefallene Marker entfernen
    (state.markers.keys - titles).forEach { title ->
        state.animations.remove(title)?.cancel()
        state.markers.remove(title)?.let { map.overlays.remove(it) }
    }

    markers.forEach { item ->
        val target = item.coordinate.toGeoPoint()
        val existing = state.markers[item.title]
        if (existing == null) {
            val marker = Marker(map).apply {
                position = target
                title = item.title
            }
            val car = if (item.isEmployee) carIcon(map.context) else null
            if (car != null) {
                marker.icon = car
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            } else {
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.icon?.constantState?.newDrawable()?.mutate()?.let { icon ->
                    icon.setTint(if (item.isEmployee) routeBlue else targetRed)
                    marker.icon = icon
                }
            }
            map.overlays.add(marker)
            state.markers[item.title] = marker
        } else if (existing.position.latitude != target.latitude || existing.position.longitude != target.longitude) {
            animateTo(map, state, item.title, existing, target)
        }
    }
}

// Auto gleitet zur neuen Position statt zu springen
private fun animateTo(map: MapView, state: MapState, key: String, marker: Marker, target: GeoPoint) {
    state.animations.remove(key)?.cancel()
    val from = GeoPoint(marker.position.latitude, marker.position.longitude)

    val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1_500
        interpolator = LinearInterpolator()
        addUpdateListener { animation ->
            val t = animation.animatedFraction.toDouble()
            marker.position = GeoPoint(
                from.latitude + (target.latitude - from.latitude) * t,
                from.longitude + (target.longitude - from.longitude) * t
            )
            map.invalidate()
        }
    }
    state.animations[key] = animator
    animator.start()
}

private fun carIcon(context: Context): Drawable? = context.getDrawable(R.drawable.ic_helper_car)

private fun Coordinate.toGeoPoint() = GeoPoint(latitude, longitude)
