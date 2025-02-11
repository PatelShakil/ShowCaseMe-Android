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
import android.webkit.WebChromeClient.FileChooserParams
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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.techsavvy.showcaseme.ui.theme.ShowCaseMeTheme

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
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val uri: Uri? = result.data?.data
                    if (uri != null) {
                        filePathCallback?.onReceiveValue(arrayOf(uri))
                    } else {
                        filePathCallback?.onReceiveValue(null)
                    }
                } else {
                    filePathCallback?.onReceiveValue(null)
                }
                filePathCallback = null
            }

        setContent {
            ShowCaseMeTheme {
                WebViewScreen() // Our Composable function below
            }
        }
    }
}

@Composable
fun WebViewScreen() {
    // Get the current context and cast it to MainActivity
    val context = LocalContext.current
    val mainActivity = context as? MainActivity ?: return

    // URL to load (adjust as needed)
    val webUrl = "http://192.168.130.51:5173/dashboard"

    // Loading state to show a progress indicator
    var isLoading by remember { mutableStateOf(false) }

    // Remember a single instance of WebView across recompositions.
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Basic WebView settings.
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.userAgentString = System.getProperty("http.agent")

            // Monitor page load events.
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    isLoading = true
                }



                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoading = false
                }
            }

            // Handle file chooser requests.
            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    // Save the callback so it can be used when a file is selected.
                    mainActivity.filePathCallback = filePathCallback

                    // Create the intent for the file chooser.
                    val intent = fileChooserParams?.createIntent()
                    try {
                        // Launch the file chooser using the Activity Result API.
                        if (intent != null) {
                            mainActivity.fileChooserLauncher.launch(intent)
                        }
                    } catch (e: ActivityNotFoundException) {
                        mainActivity.filePathCallback = null
                        return false
                    }
                    return true
                }
            }

            // Load the URL initially.
            loadUrl(webUrl)
        }
    }

    Box {
        // Show a progress indicator while loading.
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Column {
            // Embed the Android WebView in Compose.
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { webView },
                // Note: You can remove the update lambda if you do not want to reload on every recomposition.
//                update = { it.loadUrl(webUrl) }
            )
        }
    }

    // Handle back navigation for the WebView.
    BackHandler(enabled = webView.canGoBack()) {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }
}
