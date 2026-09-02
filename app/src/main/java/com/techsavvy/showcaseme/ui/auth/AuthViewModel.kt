package com.techsavvy.showcaseme.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.data.models.api_request.RegisterRequest
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import com.techsavvy.showcaseme.utils.Helpers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/** One-shot signals. Navigation and toasts must not replay on recomposition. */
sealed interface AuthEvent {
    data class Message(val text: String) : AuthEvent
    data class Error(val text: String) : AuthEvent
    /** Token is already persisted by the time this is emitted. */
    data object Authenticated : AuthEvent
    data class OtpSent(val email: String) : AuthEvent
    data object PasswordReset : AuthEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    val helper: Helpers,
) : ViewModel() {

    /** Non-null while a request is in flight; the string is the dialog copy. */
    private val _busy = MutableStateFlow<String?>(null)
    val busy: StateFlow<String?> = _busy.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun login(email: String, password: String) = viewModelScope.launch {
        _busy.value = "Signing you in\u2026"
        when (val result = authRepo.login(email.trim(), password)) {
            is Resource.Success -> handleAuthResult(
                ok = result.result.status,
                message = result.result.message,
                token = result.result.data?.token
            )
            is Resource.Failure -> _events.send(AuthEvent.Error(result.message))
            else -> Unit
        }
        _busy.value = null
    }

    fun register(request: RegisterRequest) = viewModelScope.launch {
        _busy.value = "Creating your account\u2026"
        when (val result = authRepo.register(request)) {
            is Resource.Success -> handleAuthResult(
                ok = result.result.status,
                message = result.result.message,
                token = result.result.data?.token
            )
            is Resource.Failure -> _events.send(AuthEvent.Error(result.message))
            else -> Unit
        }
        _busy.value = null
    }

    fun sendOtp(email: String) = viewModelScope.launch {
        val target = email.trim()
        _busy.value = "Sending your OTP\u2026"
        when (val result = authRepo.forgotPassword(target)) {
            is Resource.Success -> {
                if (result.result.status) {
                    _events.send(AuthEvent.OtpSent(target))
                    _events.send(AuthEvent.Message(result.result.message))
                } else {
                    _events.send(AuthEvent.Error(result.result.message))
                }
            }
            is Resource.Failure -> _events.send(AuthEvent.Error(result.message))
            else -> Unit
        }
        _busy.value = null
    }

    fun resetPassword(email: String, otp: String, password: String) = viewModelScope.launch {
        _busy.value = "Updating your password\u2026"
        when (val result = authRepo.resetPassword(email.trim(), otp.trim(), password)) {
            is Resource.Success -> {
                if (result.result.status) {
                    _events.send(AuthEvent.PasswordReset)
                    _events.send(AuthEvent.Message(result.result.message))
                } else {
                    _events.send(AuthEvent.Error(result.result.message))
                }
            }
            is Resource.Failure -> _events.send(AuthEvent.Error(result.message))
            else -> Unit
        }
        _busy.value = null
    }

    private suspend fun handleAuthResult(ok: Boolean, message: String, token: String?) {
        if (ok && !token.isNullOrBlank()) {
            helper.saveString(TOKEN_KEY, token)
            _events.send(AuthEvent.Message(message))
            _events.send(AuthEvent.Authenticated)
        } else {
            _events.send(AuthEvent.Error(message.ifBlank { "Something went wrong. Please try again." }))
        }
    }

    companion object {
        const val TOKEN_KEY = "token"
    }
}
