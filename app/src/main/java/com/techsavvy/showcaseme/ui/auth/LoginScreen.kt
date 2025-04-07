package com.techsavvy.showcaseme.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.techsavvy.showcaseme.ui.nav.Screens

@Composable
fun LoginScreen(navController: NavController,viewModel:AuthViewModel){
    Column(){
        Text("Login Screen")
        Button(onClick = {
            navController.navigate(Screens.Home.route)
        }) {
            Text("Go to home")
        }
    }
}