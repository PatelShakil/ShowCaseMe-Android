package com.techsavvy.showcaseme.push

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.techsavvy.showcaseme.MainActivity
import com.techsavvy.showcaseme.R
import com.techsavvy.showcaseme.data.repo.api.device.DeviceRepo
import com.techsavvy.showcaseme.utils.Helpers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

/**
 * Receives pushes from Firebase.
 *
 * The API sends Android messages data-only, so this always runs — including
 * while the app is in the background — and builds the notification itself. That
 * is what lets a tap carry the owner to the exact record instead of just
 * opening the app.
 */
@AndroidEntryPoint
class PushMessagingService : FirebaseMessagingService() {

    @Inject lateinit var helpers: Helpers
    @Inject lateinit var deviceRepo: DeviceRepo

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Firebase rotates registration tokens. The new one is stored and sent to
     * the API, so the server never keeps pushing to a token that has expired.
     */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed")

        helpers.saveString(PushRegistrar.FCM_TOKEN_KEY, token)

        val authToken = helpers.getString(PushRegistrar.AUTH_TOKEN_KEY)
        if (authToken.isEmpty()) {
            // Nobody is signed in yet; PushRegistrar sends it after login.
            return
        }

        scope.launch {
            deviceRepo.register(authToken, token, PushRegistrar.deviceLabel())
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        // Fall back to the notification block so a message sent from the
        // Firebase console during testing still shows something sensible.
        val title = data["title"] ?: message.notification?.title ?: "AapdiWebsite"
        val body = data["body"] ?: message.notification?.body.orEmpty()
        val url = data["url"] ?: "/dashboard"

        show(title, body, url, data["type"])
    }

    private fun show(title: String, body: String, url: String, type: String?) {
        PushNotifications.ensureChannel(this)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(PushNotifications.EXTRA_URL, url)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            // A distinct request code per notification keeps each one's extras,
            // instead of every tap reusing the first url.
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, PushNotifications.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Grouping by category means a run of enquiries replaces itself rather
        // than burying the owner in separate entries.
        val id = type?.hashCode() ?: Random.nextInt()

        try {
            NotificationManagerCompat.from(this).notify(id, notification)
        } catch (e: SecurityException) {
            // Permission was revoked between registering and this message.
            Log.w(TAG, "Notification not shown: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "PushMessaging"
    }
}
