package com.example.careconnect.platform

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import com.example.careconnect.domain.model.Coordinate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class AndroidGeocoder(
    private val context: Context
) : PlatformGeocoder {

    override suspend fun geocode(address: String): Coordinate? {
        if (address.isBlank() || !Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale.GERMANY)

        val result: Address? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCancellableCoroutine { cont ->
                geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        cont.resume(addresses.firstOrNull())
                    }

                    override fun onError(errorMessage: String?) {
                        cont.resume(null)
                    }
                })
            }
        } else {
            withContext(Dispatchers.IO) {
                @Suppress("DEPRECATION")
                runCatching { geocoder.getFromLocationName(address, 1)?.firstOrNull() }.getOrNull()
            }
        }

        return result?.let { Coordinate(it.latitude, it.longitude) }
    }
}
