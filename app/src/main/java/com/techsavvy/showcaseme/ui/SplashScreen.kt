package com.techsavvy.showcaseme.ui

import android.window.SplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.techsavvy.showcaseme.ui.nav.Screens

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(true) {
        navController.navigate(Screens.Login.route)
        {
            popUpTo(Screens.Splash.route) {
                inclusive = true
            }
        }
    }
}