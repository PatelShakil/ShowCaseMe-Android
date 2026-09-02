package com.techsavvy.showcaseme.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.isValidEmail
import com.techsavvy.showcaseme.common.isValidPassword
import com.techsavvy.showcaseme.ui.auth.components.AuthScaffold
import com.techsavvy.showcaseme.ui.auth.components.BrandPasswordField
import com.techsavvy.showcaseme.ui.auth.components.BrandPrimaryButton
import com.techsavvy.showcaseme.ui.auth.components.BrandTextField
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast

/**
 * Native replacement for the old "open the website in a browser" forgot-password
 * link.
 *
 * Step 1  POST /api/auth/forgot-password { email }        -> mails a 6-digit OTP
 * Step 2  POST /api/auth/reset-password  { email, otp, password }
 *
 * The OTP is valid for 10 minutes and the API refuses a new password identical
 * to the current one.
 */
@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: AuthViewModel) {
    val toast = LocalSmartToast.current
    val busy by viewModel.busy.collectAsState()

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var otpStage by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val emailError = submitted && !isValidEmail(email)
    val otpError = otpStage && submitted && otp.length != 6
    val passwordError = otpStage && submitted && !isValidPassword(password)
    val confirmError = otpStage && submitted && confirmPassword != password

    fun requestOtp() {
        submitted = true
        if (!isValidEmail(email)) {
            toast.show("Enter a valid email address")
            return
        }
        submitted = false
        viewModel.sendOtp(email)
    }

    fun resetPassword() {
        submitted = true
        when {
            otp.length != 6 -> toast.show("Enter the 6-digit OTP")
            !isValidPassword(password) -> toast.show("Password must be at least 6 characters")
            confirmPassword != password -> toast.show("Passwords do not match")
            else -> viewModel.resetPassword(email, otp, password)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.Message -> toast.show(event.text)
                is AuthEvent.Error -> toast.show(event.text)
                is AuthEvent.OtpSent -> {
                    otpStage = true
                    submitted = false
                }
                AuthEvent.PasswordReset -> navController.navigate(Screens.Login.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
                else -> Unit
            }
        }
    }

    busy?.let { PremiumLoadingDialog(message = it, show = true) }

    AuthScaffold(
        title = if (otpStage) "Enter your OTP" else "Reset your password",
        subtitle = if (otpStage) {
            "We emailed a 6-digit code to $email. It expires in 10 minutes."
        } else {
            "Enter the email on your account and we will send you a one-time code."
        },
        onBack = { navController.popBackStack() }
    ) {
        BrandTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            imeAction = if (otpStage) ImeAction.Next else ImeAction.Done,
            isError = emailError,
            supportingText = if (emailError) "Enter a valid email address" else null,
            onImeDone = { if (!otpStage) requestOtp() }
        )

        AnimatedVisibility(visible = otpStage) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                BrandTextField(
                    value = otp,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 6) otp = digits
                    },
                    label = "6-digit OTP",
                    icon = Icons.Default.Pin,
                    keyboardType = KeyboardType.NumberPassword,
                    isError = otpError,
                    supportingText = if (otpError) "The code is 6 digits" else null
                )

                BrandPasswordField(
                    value = password,
                    onValueChange = { if (it.length <= 100) password = it },
                    label = "New password",
                    icon = Icons.Default.Lock,
                    isError = passwordError,
                    supportingText = if (passwordError) "At least 6 characters" else "At least 6 characters"
                )

                BrandPasswordField(
                    value = confirmPassword,
                    onValueChange = { if (it.length <= 100) confirmPassword = it },
                    label = "Confirm new password",
                    icon = Icons.Default.Lock,
                    imeAction = ImeAction.Done,
                    isError = confirmError,
                    supportingText = if (confirmError) "Passwords do not match" else null,
                    onImeDone = { resetPassword() }
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        BrandPrimaryButton(
            text = if (otpStage) "Update password" else "Send OTP",
            onClick = { if (otpStage) resetPassword() else requestOtp() },
            loading = busy != null
        )

        if (otpStage) {
            TextButton(onClick = { viewModel.sendOtp(email) }) {
                Text(
                    "Did not get the code? Resend",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
