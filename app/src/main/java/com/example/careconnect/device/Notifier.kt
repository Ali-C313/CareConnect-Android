package com.example.careconnect.device

// Lokale Benachrichtigung über den NotificationManager
interface Notifier {
    fun show(id: String, title: String, text: String)
}
