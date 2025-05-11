package com.techsavvy.showcaseme.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.ui.nav.Screens
import com.techsavvy.showcaseme.widgets.PremiumLoadingDialog

@Composable
fun SplashScreen(navController: NavController,viewModel: SplashViewModel) {


    viewModel.loginState.value.let {
        when(it){
            is Resource.Loading ->{
                PremiumLoadingDialog(message = "Wait.....",true)
            }

            is Resource.Failure -> {
                LaunchedEffect(true) {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            }
            is Resource.Success -> {
                LaunchedEffect(true) {
                    if(it.result.status) {
                        navController.navigate(Screens.Home.route) {
                            popUpTo(Screens.Splash.route) {
                                inclusive = true
                            }
                        }
                    }else{
                        navController.navigate(Screens.Login.route) {
                            popUpTo(Screens.Splash.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
            else -> {}
        }
    }
}