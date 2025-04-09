package com.techsavvy.showcaseme.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.techsavvy.showcaseme.ui.nav.Screens

@Composable
fun HomeScreen(navController: NavController,viewModel:HomeViewModel) {
    WebViewScreen(navController,viewModel)
}