package com.techsavvy.showcaseme.widgets.utils

// ToastProvider.kt


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SmartToast(toastState: SmartToastState) {
    val scope = rememberCoroutineScope()

    if (toastState.isVisible) {
        LaunchedEffect(toastState.message) {
            scope.launch {
                toastState.autoHide()
            }
        }
    }

    AnimatedVisibility(
        visible = toastState.isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.9f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = toastState.message,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}


class SmartToastState {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)

    fun show(msg: String) {
        message = msg
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }

    suspend fun autoHide(delayMillis: Long = 2500) {
        delay(delayMillis)
        hide()
    }
}


val LocalSmartToast = compositionLocalOf<SmartToastState> {
    error("No SmartToastState provided")
}
