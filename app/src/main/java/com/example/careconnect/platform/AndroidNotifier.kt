package com.example.careconnect.platform

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.careconnect.R
import com.example.careconnect.util.ResourceHelper

class AndroidNotifier(
    private val context: Context
) : Notifier {

    private val manager = context.getSystemService(NotificationManager::class.java)

    init {
        val channel = NotificationChannel(
            CHANNEL_ID,
            ResourceHelper.getString(R.string.auftraege),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = ResourceHelper.getString(R.string.neue_auftraege_verordnungen_aenderungen)
        }
        manager.createNotificationChannel(channel)
    }

    override fun show(id: String, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        // Tippen öffnet die App
        val openApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.let { intent ->
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openApp)
            .build()

        manager.notify(id.hashCode(), notification)
    }

    private companion object {
        const val CHANNEL_ID = "careconnect_orders"
    }
}
