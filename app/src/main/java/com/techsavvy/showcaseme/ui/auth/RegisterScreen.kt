package com.techsavvy.showcaseme.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.isValidEmail
import com.techsavvy.showcaseme.common.isValidPassword
import com.techsavvy.showcaseme.common.isValidPhone
import com.techsavvy.showcaseme.common.isValidPincode
import com.techsavvy.showcaseme.common.orNullIfBlank
import com.techsavvy.showcaseme.data.models.api_request.RegisterRequest
import com.techsavvy.showcaseme.ui.auth.components.AuthScaffold
import com.techsavvy.showcaseme.ui.auth.components.AuthSwitchRow
import com.techsavvy.showcaseme.ui.auth.components.BrandPasswordField
import com.techsavvy.showcaseme.ui.auth.components.BrandPrimaryButton
import com.techsavvy.showcaseme.ui.auth.components.BrandTextField
import com.techsavvy.showcaseme.ui.auth.components.SectionHeading
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast

/**
 * POST /api/auth/register.
 *
 * Every field below is one the API actually validates and persists:
 * name, email, phone, password (required set) plus optional state, city and
 * pincode. The website slug is chosen later in the dashboard onboarding wizard,
 * and the avatar is set from the profile screen (POST /api/upload then
 * PUT /api/my/profile) — neither belongs on the signup form.
 */
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    val toast = LocalSmartToast.current
    val busy by viewModel.busy.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    var showLocation by remember { mutableStateOf(false) }
    var submitted by remember { mutableStateOf(false) }

    val nameError = submitted && name.isBlank()
    val emailError = submitted && !isValidEmail(email)
    val phoneError = submitted && phone.isNotBlank() && !isValidPhone(phone)
    val passwordError = submitted && !isValidPassword(password)
    val confirmError = submitted && confirmPassword != password
    val pincodeError = submitted && !isValidPincode(pincode)

    fun submit() {
        submitted = true
        when {
            name.isBlank() -> toast.show("Enter your name")
            name.length > 100 -> toast.show("Name must be 100 characters or fewer")
            !isValidEmail(email) -> toast.show("Enter a valid email address")
            phone.isNotBlank() && !isValidPhone(phone) -> toast.show("Enter a valid phone number")
            !isValidPassword(password) -> toast.show("Password must be at least 6 characters")
            confirmPassword != password -> toast.show("Passwords do not match")
            !isValidPincode(pincode) -> toast.show("Pincode must be digits only")
            else -> viewModel.register(
                RegisterRequest(
                    name = name.trim(),
                    email = email.trim(),
                    password = password,
                    phone = phone.orNullIfBlank(),
                    state = state.orNullIfBlank(),
                    city = city.orNullIfBlank(),
                    pincode = pincode.orNullIfBlank(),
                )
            )
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
        title = "Create your account",
        subtitle = "One account runs your website, product catalogue and WhatsApp enquiries.",
        onBack = { navController.popBackStack() },
        footer = {
            AuthSwitchRow(
                prompt = "Already registered?",
                action = "Sign in"
            ) { navController.popBackStack() }
        }
    ) {
        BrandTextField(
            value = name,
            onValueChange = { if (it.length <= 100) name = it },
            label = "Full name",
            icon = Icons.Default.Person,
            keyboardType = KeyboardType.Text,
            isError = nameError,
            supportingText = if (nameError) "Name is required" else null
        )

        BrandTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            isError = emailError,
            supportingText = if (emailError) "Enter a valid email address" else null
        )

        BrandTextField(
            value = phone,
            onValueChange = { input ->
                val cleaned = input.filter { it.isDigit() || it == '+' || it == ' ' }
                if (cleaned.length <= 20) phone = cleaned
            },
            label = "Phone (optional)",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            isError = phoneError,
            supportingText = when {
                phoneError -> "Enter a valid phone number"
                else -> "Used for WhatsApp enquiries from your website"
            }
        )

        BrandPasswordField(
            value = password,
            onValueChange = { if (it.length <= 100) password = it },
            label = "Password",
            icon = Icons.Default.Lock,
            isError = passwordError,
            supportingText = if (passwordError) "At least 6 characters" else "At least 6 characters"
        )

        BrandPasswordField(
            value = confirmPassword,
            onValueChange = { if (it.length <= 100) confirmPassword = it },
            label = "Confirm password",
            icon = Icons.Default.Lock,
            imeAction = ImeAction.Done,
            isError = confirmError,
            supportingText = if (confirmError) "Passwords do not match" else null,
            onImeDone = { submit() }
        )

        Spacer(Modifier.height(2.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showLocation = !showLocation }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                SectionHeading("Business location")
                Text(
                    text = "Optional — helps customers find you",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = if (showLocation) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (showLocation) "Hide location fields" else "Show location fields",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        AnimatedVisibility(visible = showLocation) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                BrandTextField(
                    value = state,
                    onValueChange = { if (it.length <= 100) state = it },
                    label = "State",
                    icon = Icons.Default.Map
                )
                BrandTextField(
                    value = city,
                    onValueChange = { if (it.length <= 100) city = it },
                    label = "City",
                    icon = Icons.Default.LocationCity
                )
                BrandTextField(
                    value = pincode,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 10) pincode = digits
                    },
                    label = "Pincode",
                    icon = Icons.Default.PinDrop,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done,
                    isError = pincodeError,
                    supportingText = if (pincodeError) "Digits only, up to 10" else null,
                    onImeDone = { submit() }
                )
            }
        }

        Spacer(Modifier.height(2.dp))

        BrandPrimaryButton(
            text = "Create account",
            onClick = { submit() },
            loading = busy != null
        )
    }
}
