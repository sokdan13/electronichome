package com.example.electronichome.presentation.navigation

sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Register : Screen("register")
    object Home     : Screen("home")
    object Meters   : Screen("meters")
    object Requests : Screen("requests")
    object Profile      : Screen("profile")
    object Announcements: Screen("announcements")
    object Apartments   : Screen("apartments")
    object AddApartment : Screen("add_apartment")
}
