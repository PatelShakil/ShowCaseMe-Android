package com.techsavvy.showcaseme.ui.home

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.Resource
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
    val jsBridge: JSBridge
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

    fun setNav(navController: NavController?) {
        jsBridge.onNavigateLogin = {
            helper.remove("token")
            viewModelScope.launch(Dispatchers.Main) {
                navController?.navigate(Screens.Login.route) {
                    popUpTo(navController.currentDestination?.parent?.route.toString()) {
                        inclusive = true
                    }
                }
            }
        }
        jsBridge.onNavigateQR = {
            viewModelScope.launch(Dispatchers.Main) {
                webView?.saveState(webViewState)
                navController?.navigate(Screens.QRScreen.route)
            }
        }
//        jsBridge.onNavigateLogin?.invoke()
    }
}