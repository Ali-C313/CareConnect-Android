package com.example.careconnect.remote.api

import com.example.careconnect.remote.model.GeoapifyResponse
import com.example.careconnect.remote.model.GeoapifyRouteResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val GEOAPIFY_BASE_URL = "https://api.geoapify.com/v1/"

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
    .baseUrl(GEOAPIFY_BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

// Adresssuche, Geocoding und Routing (nur mit eigenem Key)
interface GeoapifyApiService {

    @GET("geocode/autocomplete")
    suspend fun autocomplete(
        @Query("text") text: String,
        @Query("apiKey") apiKey: String,
        @Query("limit") limit: Int = 5,
        @Query("lang") lang: String = "de",
        @Query("filter") filter: String = "countrycode:de",
        @Query("format") format: String = "json"
    ): Response<GeoapifyResponse>

    @GET("geocode/search")
    suspend fun search(
        @Query("text") text: String,
        @Query("apiKey") apiKey: String,
        @Query("limit") limit: Int = 1,
        @Query("lang") lang: String = "de",
        @Query("format") format: String = "json"
    ): Response<GeoapifyResponse>

    @GET("routing")
    suspend fun route(
        @Query("waypoints") waypoints: String,
        @Query("apiKey") apiKey: String,
        @Query("mode") mode: String = "drive"
    ): Response<GeoapifyRouteResponse>
}

object GeoapifyApi {
    val retrofitService: GeoapifyApiService by lazy { retrofit.create(GeoapifyApiService::class.java) }
}
