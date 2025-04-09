package com.techsavvy.showcaseme.ui.auth

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.utils.Helpers
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog
import com.techsavvy.showcaseme.widgets.utils.LocalSmartToast

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val toast = LocalSmartToast.current

    viewModel.loginState.value.let { 
        when(it){
            is Resource.Loading ->{
                PremiumLoadingDialog(message = "Login Process Started......",true)
            }
            is Resource.Failure ->{
                LaunchedEffect(true) {
                    toast.show(it.message)
                }
            }
            
            is Resource.Success ->{
                LaunchedEffect(true) {
                    toast.show(it.result.message)
                    if(it.result.status) {
                        viewModel.helper.saveString("token", it.result.data?.token.toString())
                        navController.navigate(Screens.Home.route) {
                            popUpTo(Screens.Login.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
            else->{}
        }
    }
    

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFCEE9FE)) // white to #CEE9FE
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("ShowCaseMe",
                    style = MaterialTheme.typography.bodyMedium
                    )
                Text("Welcome Back 👋", style = MaterialTheme.typography.headlineSmall)
                Text("Login to your account", fontSize = 14.sp, color = Color.Gray)

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                    trailingIcon = {
                        val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = icon, contentDescription = "Toggle Password Visibility")
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        // Authenticate here or call ViewModel
                        if(email.isEmpty() || password.isEmpty()){
                            toast.show("Please enter all the fields")
                            return@Button
                        }
                        viewModel.login(email,password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Login")
                }

                AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Don't have an account?", color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Register",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                navController.navigate(Screens.Register.route)
                            }
                        )
                    }
                }
            }
        }
    }
}
