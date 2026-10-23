package com.example.careconnect.mock.platform

import com.example.careconnect.platform.Notifier

// Gibt Benachrichtigungen nur in der Konsole aus
class NotifierMock : Notifier {

    override fun show(id: String, title: String, text: String) {
        println("Benachrichtigung: $title - $text")
    }
}
