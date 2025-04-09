package com.techsavvy.showcaseme.ui.home

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.techsavvy.showcaseme.MainActivity
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.URLS
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast
import kotlinx.coroutines.launch

@Composable
fun WebViewScreen(navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val mainActivity = context as? MainActivity ?: return

    var isLoading by remember { mutableStateOf(false) }

    viewModel.loginState.value.let {
        when (it) {
            is Resource.Loading -> {
                PremiumLoadingDialog(
                    message = "Please wait....",
                    show = true
                )
            }

            is Resource.Failure -> {
                LaunchedEffect(true) {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }

            is Resource.Success -> {
                val webUrl = URLS.WEB_URL + "jwt-verify/" + it.result

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
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                injectJavaScript(view)
                            }

                            private fun injectJavaScript(view: WebView?) {
                                view!!.loadUrl(
                                    """
                                javascript:(function() {
        let attempts = 0;
const maxAttempts = 20;

const interval = setInterval(function() {
    let logoutBtn = document.getElementById("logoutBtn");
    let qrBtn = document.getElementById("qrBtn");
   
    if (logoutBtn) {
        logoutBtn.addEventListener("click", function() {
            Bridge.navigateToLogin();
        });
        qrBtn.classList.remove("hidden");
        qrBtn.classList.add("flex");
        qrBtn.addEventListener("click", function() {
            Bridge.navigateToQRScreen();
        });
        clearInterval(interval);
    } else {
        attempts++;
        if (attempts > maxAttempts) {
            clearInterval(interval);
        }
    }
}, 500);

    })()
                                    """.trimIndent()
                                )


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
                                    putExtra(
                                        Intent.EXTRA_ALLOW_MULTIPLE,
                                        true
                                    ) // Enable multiple file selection
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


                        if (viewModel.webViewState.isEmpty) {
                            loadUrl(webUrl)
                        } else {
                            restoreState(viewModel.webViewState)
                        }

                        viewModel.webView = this
                    }
                }
                LaunchedEffect(Unit) {
                    viewModel.setNav(navController)
                    webView.addJavascriptInterface(viewModel.jsBridge, "Bridge")
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
                var backPressedOnce by remember { mutableStateOf(false) }
                val toast = LocalSmartToast.current
                val scope = rememberCoroutineScope()
                BackHandler(enabled = true) {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        if (backPressedOnce) {
                            mainActivity.finish()
                        } else {
                            backPressedOnce = true
                            toast.show("Press back again to exit")
                            scope.launch {
                                kotlinx.coroutines.delay(2000) // 2 seconds window
                                backPressedOnce = false
                            }
                        }
                    }
                }

            }

            else -> {}
        }
    }


}