package com.techsavvy.showcaseme.push

import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.techsavvy.showcaseme.data.repo.api.device.DeviceRepo
import com.techsavvy.showcaseme.utils.Helpers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registers this install with the API so it can receive notifications.
 *
 * Called after a successful sign in and on every launch of a signed-in app.
 * Relying on onNewToken alone is not enough: Firebase only fires that when a
 * token is first created or rotated, so someone who updates an existing
 * install would never be registered.
 */
@Singleton
class PushRegistrar @Inject constructor(
    private val helpers: Helpers,
    private val deviceRepo: DeviceRepo,
) {

    /**
     * Fetches the Firebase token and sends it to the API.
     * Safe to call repeatedly — the API upserts on the token.
     */
    suspend fun register(): Boolean {
        val authToken = helpers.getString(AUTH_TOKEN_KEY)
        if (authToken.isEmpty()) {
            return false
        }

        val fcmToken = fetchFcmToken()

        if (fcmToken == null) {
            return false
        }

        if (fcmToken.isEmpty()) return false

        helpers.saveString(FCM_TOKEN_KEY, fcmToken)

        return when (deviceRepo.register(authToken, fcmToken, deviceLabel())) {
            is com.techsavvy.showcaseme.common.Resource.Success -> {
                Log.d(TAG, "Device registered for notifications")
                true
            }
            else -> {
                Log.w(TAG, "Device registration was refused by the API")
                false
            }
        }
    }

    // FirebaseMessaging.token is marked deprecated in the current SDK but is
    // still the documented way to read the registration token, and the warning
    // carries no replacement.
    @Suppress("DEPRECATION")
    private suspend fun fetchFcmToken(): String? = suspendCancellableCoroutine { continuation ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (continuation.isActive) continuation.resume(token)
            }
            .addOnFailureListener { error ->
                Log.w(TAG, "Could not obtain an FCM token: ${error.message}")
                if (continuation.isActive) continuation.resume(null)
            }
    }

    /** Called on sign out so the server stops pushing to this handset. */
    suspend fun unregister() {
        val authToken = helpers.getString(AUTH_TOKEN_KEY)
        val fcmToken = helpers.getString(FCM_TOKEN_KEY)

        if (authToken.isEmpty() || fcmToken.isEmpty()) return

        runCatching { deviceRepo.unregister(authToken, fcmToken) }
        helpers.remove(FCM_TOKEN_KEY)
    }

    companion object {
        private const val TAG = "PushRegistrar"

        /** The existing auth key; notifications reuse it rather than adding one. */
        const val AUTH_TOKEN_KEY = "token"
        const val FCM_TOKEN_KEY = "fcm_token"

        /** Shown in the owner's device list, so they can tell handsets apart. */
        fun deviceLabel(): String =
            listOfNotNull(Build.MANUFACTURER?.replaceFirstChar { it.uppercase() }, Build.MODEL)
                .joinToString(" ")
                .trim()
                .ifEmpty { "Android device" }
    }
}
