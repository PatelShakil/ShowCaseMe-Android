package com.techsavvy.showcaseme

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.navigation.NavController

@Composable
fun WebViewScreen(navController : NavController) {
    val context = LocalContext.current
    val mainActivity = context as? MainActivity ?: return

    val webUrl = "http://192.168.6.51:5173/dashboard"
    var isLoading by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.allowFileAccess = true
            settings.userAgentString = System.getProperty("http.agent")

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

            webChromeClient = object : WebChromeClient() {
                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    mainActivity.filePathCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent()?.apply {
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Enable multiple file selection
                    }

                    return try {
                        if (intent != null) {
                            mainActivity.fileChooserLauncher.launch(intent)
                        }
                        true
                    } catch (e: ActivityNotFoundException) {
                        mainActivity.filePathCallback = null
                        false
                    }
                }
            }

            loadUrl(webUrl)
        }
    }

    Box {
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Column {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { webView }
            )
        }
    }

    BackHandler(enabled = webView.canGoBack()) {
        if (webView.canGoBack()) {
            webView.goBack()
        }else{
            navController.popBackStack()
        }
    }
}