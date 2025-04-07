package com.techsavvy.showcaseme

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.techsavvy.showcaseme.ui.nav.AppNavHost
import com.techsavvy.showcaseme.ui.theme.ShowCaseMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // This callback will be used to send file URIs back to the WebView.
    var filePathCallback: ValueCallback<Array<Uri>>? = null

    // Register a launcher with the new Activity Result API to handle file chooser intents.
    lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register the file chooser launcher.  
        fileChooserLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (filePathCallback == null) return@registerForActivityResult

                val data: Intent? = result.data
                val uris: Array<Uri>? = when {
                    result.resultCode != Activity.RESULT_OK -> null
                    data?.clipData != null -> {  // Handle multiple selected files
                        val clipData = data.clipData!!
                        Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                    }
                    data?.data != null -> arrayOf(data.data!!) // Handle single file selection
                    else -> null
                }

                filePathCallback?.onReceiveValue(uris)
                filePathCallback = null
            }

        setContent {
            ShowCaseMeTheme {
                AppNavHost()
            }
        }
    }
}


