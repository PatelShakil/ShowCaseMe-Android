package com.techsavvy.showcaseme.ui.auth

import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.common.drawableToFile
import com.techsavvy.showcaseme.common.isValidUsername
import com.techsavvy.showcaseme.data.models.UserModel
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast
import com.techsavvy.showcaseme.widgets.utils.loadBitmapFromLocalStorage
import com.techsavvy.showcaseme.widgets.utils.rememberGetContentContractLauncher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var profile by remember { mutableStateOf<Drawable?>(null) }
    var profileStr by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val toast = LocalSmartToast.current
    var usernameError by remember { mutableStateOf("") }

    val getProfileImage = rememberGetContentContractLauncher(onResult = {
        if (it != null) {
            profileStr = it.toString()
            loadBitmapFromLocalStorage(context, profileStr, {
                profile = it.toDrawable(context.resources)
            }) {

            }
        }
    })

    val coroutineScope = rememberCoroutineScope()
    var usernameDebounceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(username) {
        usernameDebounceJob?.cancel()
        usernameDebounceJob = coroutineScope.launch {
            delay(500) // 500ms debounce
            if (isValidUsername(username)) {
                viewModel.checkUserExists(username)
                usernameError = ""
                println("Debounced username Input: $username")
            } else {
                println("Invalid username entered: $username")
                // Optionally, you can show an error state or Snackbar here
                if (username.isNotEmpty()) {
                    usernameError = "Invalid username characters!"
                }
            }
        }
    }



    viewModel.signupState.value.let {
        when (it) {
            is Resource.Loading -> {
                PremiumLoadingDialog(message = "Registration Process Started......", true)
            }

            is Resource.Failure -> {
                LaunchedEffect(true) {
                    toast.show(it.message)
                }
            }

            is Resource.Success -> {
                LaunchedEffect(true) {
                    toast.show(it.result.message)
                    if (it.result.status) {
                        viewModel.helper.saveString("token", it.result.data?.token.toString())
                        navController.navigate(Screens.Home.route) {
                            popUpTo(Screens.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            else -> {}
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFCEE9FE),
                        Color(0xFFCEE9FE),
                        Color(0xFFCEE9FE),
                    ) // white to #CEE9FE
                )
            )
            .padding(16.dp),
    ) {

        Text(
            "ShowCaseMe",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFCEE9FE),
                            Color(0xFFCEE9FE),
                        )
                    )
                )
                .padding(10.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.DarkGray,
            fontWeight = Bold
        )
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text("Get Started 🏁", style = MaterialTheme.typography.headlineSmall)
//                Text("Create your account", fontSize = 14.sp, color = Color.Gray)
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (profile != null) Arrangement.Center else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (profile != null) {
                    Image(
                        profile?.toBitmap(1024, 1024)!!.asImageBitmap(),
                        "",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(15.dp))
                }
                Button(
                    {
                        if (profile == null) {
                            getProfileImage.launch("image/*")
                        } else {
                            profile = null
                        }
                    }) {
                    Text(
                        if (profile == null) "Choose Profile" else "Remove"
                    )
                }

            }


            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it.lowercase().filter { char -> char.isLetterOrDigit() || char == '_' }
                },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth(),
                isError = when {
                    username.isEmpty() -> false // avoid red on empty input
                    !isValidUsername(username) -> true
                    viewModel.checkUserExists.value is Resource.Success -> {
                        !(viewModel.checkUserExists.value as Resource.Success<Response<String?>>).result.status
                    }
                    else -> false
                },
                supportingText = {
                    viewModel.checkUserExists.value.let {
                        when (it) {
                            is Resource.Loading -> {
                                Text("Checking...")
                            }

                            is Resource.Success -> {
                                Text(if (usernameError.isNotEmpty()) usernameError else it.result.message)
                            }

                            is Resource.Failure -> {
                                Text(it.message)
                            }

                            else -> {}
                        }
                    }
                }

            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = bio,
                onValueChange = { bio = it },
                label = { Text("Bio") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    // Authenticate here or call ViewModel
                    when{
                      !isValidUsername(username) -> {
                            toast.show("Invalid Username")
                            return@Button
                        }
                        name.isEmpty() -> {
                            toast.show("Name cannot be empty")
                            return@Button
                        }
                        email.isEmpty() -> {
                            toast.show("Email cannot be empty")
                            return@Button
                        }
                        phone.isEmpty() -> {
                            toast.show("Phone cannot be empty")
                            return@Button
                        }
                        bio.isEmpty() -> {
                            toast.show("Bio cannot be empty")
                            return@Button
                        }
                        password.isEmpty() -> {
                            toast.show("Password cannot be empty")
                            return@Button
                        }
                        profile == null -> {
                            toast.show("Please Select Profile Image")
                            return@Button
                        }
                        else -> {
                            viewModel.signup(
                                UserModel(
                                    username = username,
                                    name = name,
                                    email = email,
                                    phone = phone,
                                    bio = bio,
                                    password = password
                                ), drawableToFile(context,profile!!)
                            )
                        }
                    }



                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Register")
            }

            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", color = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Login",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            navController.popBackStack()
                        })
                }

            }
        }
    }
}