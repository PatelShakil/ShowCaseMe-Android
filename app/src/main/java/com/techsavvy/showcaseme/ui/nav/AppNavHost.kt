package com.techsavvy.showcaseme.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techsavvy.showcaseme.ui.SplashScreen
import com.techsavvy.showcaseme.ui.auth.LoginScreen
import com.techsavvy.showcaseme.ui.home.HomeScreen

@Composable
fun AppNavHost() {

    var navController = rememberNavController()

    Scaffold {

        NavHost(
            navController,
            startDestination = Screens.Splash.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screens.Splash.route) {
                SplashScreen(navController)
            }
            composable(Screens.Login.route) {
                LoginScreen(navController, hiltViewModel())
            }
            composable(Screens.Home.route){
                HomeScreen(navController,hiltViewModel())
            }
        }
    }
}