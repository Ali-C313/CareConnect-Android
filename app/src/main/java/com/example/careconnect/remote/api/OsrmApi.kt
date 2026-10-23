package com.example.careconnect.remote.api

import com.example.careconnect.remote.model.OsrmResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val OSRM_BASE_URL = "https://router.project-osrm.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// API-Calls im Logcat mitlesen
private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()

private val retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl(OSRM_BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

// Route mit Straßenverlauf, braucht keinen Key
interface OsrmApiService {

    // points = "lon,lat;lon,lat", darf nicht kodiert werden
    @GET("route/v1/driving/{points}")
    suspend fun route(
        @Path("points", encoded = true) points: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson"
    ): Response<OsrmResponse>
}

object OsrmApi {
    val retrofitService: OsrmApiService by lazy { retrofit.create(OsrmApiService::class.java) }
}
