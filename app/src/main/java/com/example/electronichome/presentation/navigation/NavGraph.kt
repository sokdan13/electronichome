package com.example.electronichome.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.electronichome.presentation.auth.LoginScreen
import com.example.electronichome.presentation.auth.RegisterScreen
import com.example.electronichome.presentation.apartments.AddApartmentScreen
import com.example.electronichome.presentation.screens.AnnouncementsScreen
import com.example.electronichome.presentation.apartments.ApartmentsScreen
import com.example.electronichome.presentation.home.HomeScreen
import com.example.electronichome.presentation.screens.MetersScreen
import com.example.electronichome.presentation.screens.ProfileScreen
import com.example.electronichome.presentation.screens.RequestsScreen
import com.google.firebase.auth.FirebaseAuth

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Announcements.route, "Объявления",  Icons.Outlined.Notifications),
    BottomNavItem(Screen.Home.route,          "Главная",     Icons.Outlined.Home),
    BottomNavItem(Screen.Profile.route,       "Профиль",     Icons.Outlined.Person),
)

private val bottomBarRoutes = bottomNavItems.map { it.route }.toSet()

@Composable
fun AppNavGraph(navController: NavHostController) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null)
        Screen.Home.route else Screen.Login.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Announcements.route) {
                AnnouncementsScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToApartments = {
                        navController.navigate(Screen.Apartments.route)
                    },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Apartments.route) {
                ApartmentsScreen(
                    onAddApartment = { navController.navigate(Screen.AddApartment.route) }
                )
            }
            composable(Screen.AddApartment.route) {
                AddApartmentScreen(
                    onSuccess      = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Meters.route) {
                MetersScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(Screen.Requests.route) {
                RequestsScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}





