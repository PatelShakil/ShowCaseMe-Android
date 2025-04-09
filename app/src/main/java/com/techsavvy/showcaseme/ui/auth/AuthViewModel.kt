package com.techsavvy.showcaseme.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.showcaseme.utils.Helpers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo : AuthRepo,
    val helper : Helpers
): ViewModel(){

    private var _loginState = mutableStateOf<Resource<Response<LoginResponse>>?>(null)
    val loginState = _loginState

    fun login(email : String, password : String) = viewModelScope.launch {
        _loginState.value = Resource.Loading
        _loginState.value = authRepo.login(email, password)
    }
}