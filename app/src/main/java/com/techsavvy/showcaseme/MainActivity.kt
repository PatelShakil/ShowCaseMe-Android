package com.techsavvy.showcaseme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.techsavvy.showcaseme.push.PushNotifications
import com.techsavvy.showcaseme.ui.nav.AppNavHost
import com.techsavvy.showcaseme.ui.theme.ShowCaseMeTheme
import com.techsavvy.showcaseme.utils.InAppUpdateManager
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast
import com.techsavvy.showcaseme.widgets.utils.SmartToast
import com.techsavvy.showcaseme.widgets.utils.SmartToastState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var inAppUpdateManager: InAppUpdateManager

    var filePathCallback: ValueCallback<Array<Uri>>? = null

    /**
     * The dashboard path a tapped notification wants opened.
     *
     * The activity is singleTask, so a tap on a running app arrives through
     * onNewIntent rather than a fresh instance. WebViewScreen watches this and
     * navigates in place, which avoids a second token handoff.
     */
    val pendingNotificationUrl = MutableStateFlow<String?>(null)
    lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>
    private val updateActivityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                Log.e("MainActivity", "Update flow failed! Result code: ${result.resultCode}")
                // If mandatory update was cancelled/failed, re-trigger check
                inAppUpdateManager.checkForAppUpdate(updateActivityResultLauncher)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // From Android 15 the system draws edge to edge whether an app asks or
        // not, so the insets are declared here and every screen pads for them.
        // Without this the content sits under the status bar.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateManager.checkForAppUpdate(updateActivityResultLauncher)

        fileChooserLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (filePathCallback == null) return@registerForActivityResult
                val data: Intent? = result.data
                val uris: Array<Uri>? = when {
                    result.resultCode != Activity.RESULT_OK -> null
                    data?.clipData != null -> {
                        val clipData = data.clipData!!
                        Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                    }
                    data?.data != null -> arrayOf(data.data!!)
                    else -> null
                }
                filePathCallback?.onReceiveValue(uris)
                filePathCallback = null
            }

        // A cold launch from a notification carries the path in its extras.
        pendingNotificationUrl.value = intent?.getStringExtra(PushNotifications.EXTRA_URL)

        setContent {
            ShowCaseMeTheme {
                val toastState = remember { SmartToastState() }
                CompositionLocalProvider(LocalSmartToast provides toastState) {
                    AppNavHost()
                    SmartToast(toastState)
                }
            }
        }
    }



    /** Reached when the app is already running and a notification is tapped. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(PushNotifications.EXTRA_URL)?.let {
            pendingNotificationUrl.value = it
        }
    }

    override fun onResume() {
        super.onResume()
        if (::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.resumeUpdateIfInProgress(updateActivityResultLauncher)
        }
    }
}
