package com.techsavvy.showcaseme.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Notification plumbing shared by the messaging service and the launcher.
 */
object PushNotifications {

    /** Must match the default_notification_channel_id in the manifest. */
    const val CHANNEL_ID = "aapdi_activity"

    /** Intent extra carrying the dashboard path a notification should open. */
    const val EXTRA_URL = "aapdi_notification_url"

    /**
     * Created when the app starts rather than when the first message lands.
     * Android 8 and above silently discard a notification posted to a channel
     * that does not exist yet.
     */
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager = context.getSystemService(NotificationManager::class.java) ?: return

        if (manager.getNotificationChannel(CHANNEL_ID) != null) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Business activity",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Enquiries, messages and Instagram activity for your website."
            enableVibration(true)
            setShowBadge(true)
        }

        manager.createNotificationChannel(channel)
    }
}
