package com.techsavvy.showcaseme.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.techsavvy.showcaseme.WebViewScreen

@Composable
fun HomeScreen(navController: NavController,viewModel:HomeViewModel) {
    WebViewScreen(navController)
}