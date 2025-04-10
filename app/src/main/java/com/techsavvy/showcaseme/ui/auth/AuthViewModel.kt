package com.techsavvy.showcaseme.ui.auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.showcaseme.data.models.UserModel
import com.techsavvy.showcaseme.data.repo.user.UserRepo
import com.techsavvy.showcaseme.utils.Helpers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo : AuthRepo,
    val helper : Helpers,
    private val userRepo: UserRepo
): ViewModel(){

    private var _loginState = mutableStateOf<Resource<Response<LoginResponse>>?>(null)
    val loginState = _loginState

    private var _signupState = mutableStateOf<Resource<Response<LoginResponse>>?>(null)
    val signupState = _signupState

    private var _checkUserExists = mutableStateOf<Resource<Response<String?>>?>(null)
    val checkUserExists = _checkUserExists

    fun checkUserExists(username : String) = viewModelScope.launch {
        _checkUserExists.value = Resource.Loading
        _checkUserExists.value = userRepo.checkUserExists(username)
    }

    fun login(email : String, password : String) = viewModelScope.launch {
        _loginState.value = Resource.Loading
        _loginState.value = authRepo.login(email, password)
    }

    fun signup(userModel: UserModel, profilePicture : File) = viewModelScope.launch {
        _signupState.value = Resource.Loading
        _signupState.value = userRepo.register(userModel, profilePicture)
    }


}