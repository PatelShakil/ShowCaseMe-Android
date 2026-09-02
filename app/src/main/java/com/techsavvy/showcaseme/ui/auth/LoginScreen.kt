package com.techsavvy.showcaseme.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.isValidEmail
import com.techsavvy.showcaseme.ui.auth.components.AuthScaffold
import com.techsavvy.showcaseme.ui.auth.components.AuthSwitchRow
import com.techsavvy.showcaseme.ui.auth.components.BrandPasswordField
import com.techsavvy.showcaseme.ui.auth.components.BrandPrimaryButton
import com.techsavvy.showcaseme.ui.auth.components.BrandTextField
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast

/**
 * POST /api/auth/login — { email, password }.
 */
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    val toast = LocalSmartToast.current
    val busy by viewModel.busy.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailError = submitted && !isValidEmail(email)
    val passwordError = submitted && password.isEmpty()

    fun submit() {
        submitted = true
        when {
            !isValidEmail(email) -> toast.show("Enter a valid email address")
            password.isEmpty() -> toast.show("Enter your password")
            else -> viewModel.login(email, password)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.Message -> toast.show(event.text)
                is AuthEvent.Error -> toast.show(event.text)
                AuthEvent.Authenticated -> navController.navigate(Screens.Home.route) {
                    popUpTo(Screens.Login.route) { inclusive = true }
                }
                else -> Unit
            }
        }
    }

    busy?.let { PremiumLoadingDialog(message = it, show = true) }

    AuthScaffold(
        title = "Welcome back",
        subtitle = "Sign in to manage your website, catalogue and customer enquiries.",
        footer = {
            AuthSwitchRow(
                prompt = "New to AapdiWebsite?",
                action = "Create account"
            ) { navController.navigate(Screens.Register.route) }
        }
    ) {
        BrandTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            isError = emailError,
            supportingText = if (emailError) "Enter a valid email address" else null
        )

        BrandPasswordField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            icon = Icons.Default.Lock,
            imeAction = ImeAction.Done,
            isError = passwordError,
            supportingText = if (passwordError) "Password is required" else null,
            onImeDone = { submit() }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { navController.navigate(Screens.ForgotPassword.route) }) {
                Text(
                    "Forgot password?",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        BrandPrimaryButton(
            text = "Sign in",
            onClick = { submit() },
            loading = busy != null
        )
    }
}
