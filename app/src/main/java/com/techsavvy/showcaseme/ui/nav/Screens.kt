package com.techsavvy.showcaseme.ui.nav

sealed class Screens(
    val route : String,val title : String = ""
){

    object Splash : Screens("splash","Splash")

    object Login : Screens("login","Login")
    object Register : Screens("register","Register")
    object Home : Screens("home","Home")
}
