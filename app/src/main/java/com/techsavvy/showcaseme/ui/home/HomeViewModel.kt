package com.techsavvy.showcaseme.ui.home

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.push.PushRegistrar
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.utils.Helpers
import com.techsavvy.showcaseme.utils.js.JSBridge
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val helper : Helpers,
    val jsBridge: JSBridge,
    private val pushRegistrar: PushRegistrar,
) : ViewModel() {

    private val _loginState = mutableStateOf<Resource<String>?>(null)
    val loginState = _loginState

    val webViewState = Bundle()
    var webView: WebView? = null

    init {
        _loginState.value = Resource.Loading
        _loginState.value = when{
            helper.getString("token").isEmpty() -> Resource.Failure(message = "Login Failed")
            else -> Resource.Success(helper.getString("token"))
        }
    }

    /**
     * Sends this install's Firebase token to the API.
     *
     * Run on every launch of a signed-in app, not only when Firebase issues a
     * new token: an existing install that updates never sees onNewToken, and
     * would otherwise never be registered.
     */
    fun registerForNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            pushRegistrar.register()
        }
    }

    fun setNav(navController: NavController?) {
        jsBridge.onNavigateLogin = {
            // Stop this handset receiving notifications for the account being
            // signed out of, before the token it needs to say so is cleared.
            viewModelScope.launch(Dispatchers.IO) { pushRegistrar.unregister() }
            helper.remove("token")
            viewModelScope.launch(Dispatchers.Main) {
                navController?.navigate(Screens.Login.route) {
                    popUpTo(Screens.Home.route) {
                        inclusive = true
                    }
                }
            }
        }
        jsBridge.onNavigateQR = {
            viewModelScope.launch(Dispatchers.Main) {
                webView?.saveState(webViewState)
                navController?.navigate(Screens.QRScreen.route)
                return@launch
            }
        }
//        jsBridge.onNavigateLogin?.invoke()
    }
}