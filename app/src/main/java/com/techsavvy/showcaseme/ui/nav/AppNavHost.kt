package com.techsavvy.showcaseme.ui.nav

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.techsavvy.showcaseme.ui.splash.SplashScreen
import com.techsavvy.showcaseme.ui.auth.LoginScreen
import com.techsavvy.showcaseme.ui.auth.RegisterScreen
import com.techsavvy.showcaseme.ui.home.HomeScreen
import com.techsavvy.showcaseme.ui.qr.GenerateQRScreen
import com.techsavvy.showcaseme.ui.qr.QRScreen
import com.techsavvy.showcaseme.ui.qr.QRShareScreen
import com.techsavvy.showcaseme.widgets.TopBar
import com.techsavvy.showcaseme.widgets.utils.getViewModelInstance

@Composable
fun AppNavHost() {

    var navController = rememberNavController()

    Scaffold(
        topBar ={
            TopBar(navController)
        }
    ) {
        NavHost(
            navController,
            startDestination = Screens.Splash.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screens.Splash.route) {
                SplashScreen(navController,hiltViewModel())
            }
            composable(Screens.Login.route) {
                LoginScreen(navController, hiltViewModel())
            }
            composable(Screens.Register.route) {
                RegisterScreen(navController,hiltViewModel())
            }
            composable(Screens.Home.route){
                HomeScreen(navController,hiltViewModel())
            }
            composable(Screens.QRScreen.route){
                GenerateQRScreen(navController,hiltViewModel())
            }
            composable(Screens.QRShare.route){
                QRShareScreen(navController,navController.getViewModelInstance(navController.getBackStackEntry(
                    Screens.QRScreen.route), Screens.QRScreen.route))
            }
        }
    }
}