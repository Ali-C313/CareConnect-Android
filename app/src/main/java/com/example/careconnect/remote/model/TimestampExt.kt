package com.example.careconnect.remote.model

import com.google.firebase.Timestamp

// Firestore-Timestamp <-> Millisekunden
fun Timestamp.toMillis(): Long = seconds * 1000 + nanoseconds / 1_000_000

fun Long.toTimestamp(): Timestamp = Timestamp(
    this.floorDiv(1000L),
    (this.mod(1000L) * 1_000_000L).toInt()
)
