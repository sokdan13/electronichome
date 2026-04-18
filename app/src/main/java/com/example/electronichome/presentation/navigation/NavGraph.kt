package com.example.electronichome.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.electronichome.presentation.auth.LoginScreen
import com.example.electronichome.presentation.auth.RegisterScreen
import com.example.electronichome.presentation.screens.HomeScreen
import com.example.electronichome.presentation.screens.MetersScreen
import com.example.electronichome.presentation.screens.RequestsScreen

import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
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
            HomeScreen(
                onNavigateToMeters   = { navController.navigate(Screen.Meters.route) },
                onNavigateToRequests = { navController.navigate(Screen.Requests.route) },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
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





