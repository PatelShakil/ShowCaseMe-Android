package com.techsavvy.showcaseme.utils.js

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.JavascriptInterface
import com.techsavvy.showcaseme.MainActivity
import com.techsavvy.showcaseme.utils.Helpers
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JSBridge @Inject constructor(
    val helpers: Helpers,
    context: Context
) {
    private val weakContext = WeakReference(context)

    var onNavigateLogin: (() -> Unit)? = null
    var onNavigateQR: (() -> Unit)? = null

    @JavascriptInterface
    fun navigateToLogin() {
        Handler(Looper.getMainLooper()).post {
            if (onNavigateLogin == null) {
                Log.e("JSBridge", "onNavigateLogin is NULL")
            }else{
                onNavigateLogin?.invoke()
            }
        }
    }
    @JavascriptInterface
    fun navigateToQRScreen() {
        Handler(Looper.getMainLooper()).post {
            if (onNavigateQR == null) {
                Log.e("JSBridge", "onNavigateQR is NULL")
            } else {
                onNavigateQR?.invoke()
            }
        }
    }

}
