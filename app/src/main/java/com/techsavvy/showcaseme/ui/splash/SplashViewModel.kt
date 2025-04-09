package com.techsavvy.showcaseme.ui.splash

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_response.JwtVerifyResponse
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import com.techsavvy.showcaseme.utils.Helpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    val helpers: Helpers
) : ViewModel() {

    private var _loginState = mutableStateOf<Resource<Response<JwtVerifyResponse>>?>(null)
    val loginState = _loginState


    init {
        jwtVerify()
    }

    fun jwtVerify() = viewModelScope.launch {
        _loginState.value = Resource.Loading
        if (helpers.getString("token").isEmpty()) {
            _loginState.value = Resource.Failure(message = "Invalid Token")
            return@launch
        }
        _loginState.value = authRepo.jwtVerify(helpers.getString("token"))
    }

}