package com.example.careconnect.platform

// Lokale Benachrichtigung über den NotificationManager
interface Notifier {
    fun show(id: String, title: String, text: String)
}
