package com.techsavvy.showcaseme.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.techsavvy.showcaseme.ui.nav.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState()

    fun getRoute(): Screens{
        return when(currentRoute.value?.destination?.route.toString()){
            Screens.Login.route -> Screens.Login
            Screens.Register.route -> Screens.Register
            Screens.ForgotPassword.route -> Screens.ForgotPassword
            Screens.Home.route -> Screens.Home
            Screens.QRScreen.route -> Screens.QRScreen
            Screens.QRShare.route -> Screens.QRShare
            else -> {
                Screens.Empty
            }
        }
    }

    // Splash and the auth flow draw their own headers, so the app bar is
    // suppressed there — showing it too gave the login screen two titles.
    val chromeLessRoutes = setOf(
        Screens.Splash.route,
        Screens.Login.route,
        Screens.Register.route,
        Screens.ForgotPassword.route,
        Screens.Home.route,
    )

    Column(modifier = Modifier.fillMaxWidth()){
        if(currentRoute.value?.destination?.route !in chromeLessRoutes) {
            AnimatedVisibility(getRoute() != Screens.Home) {
                TopAppBar(
                    navigationIcon = {
                        if (getRoute() != Screens.Login) {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Default.ArrowBack, null)
                            }
                        }
                    },
                    title = {
                        Text(getRoute().title)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    }

}