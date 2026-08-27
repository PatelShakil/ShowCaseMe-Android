package com.techsavvy.showcaseme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.techsavvy.showcaseme.ui.nav.AppNavHost
import com.techsavvy.showcaseme.ui.theme.ShowCaseMeTheme
import com.techsavvy.showcaseme.utils.InAppUpdateManager
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast
import com.techsavvy.showcaseme.widgets.utils.SmartToast
import com.techsavvy.showcaseme.widgets.utils.SmartToastState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var inAppUpdateManager: InAppUpdateManager

    var filePathCallback: ValueCallback<Array<Uri>>? = null
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



    override fun onResume() {
        super.onResume()
        if (::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.resumeUpdateIfInProgress(updateActivityResultLauncher)
        }
    }
}
