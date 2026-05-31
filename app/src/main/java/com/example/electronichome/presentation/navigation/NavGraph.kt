package com.example.electronichome.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.electronichome.data.local.UserRole
import com.example.electronichome.presentation.announcements.AnnouncementsScreen
import com.example.electronichome.presentation.apartments.AddApartmentScreen
import com.example.electronichome.presentation.apartments.ApartmentsScreen
import com.example.electronichome.presentation.apartments.ApartmentsViewModel
import com.example.electronichome.presentation.auth.AuthViewModel
import com.example.electronichome.presentation.auth.LoginScreen
import com.example.electronichome.presentation.auth.RegisterScreen
import com.example.electronichome.presentation.guestpass.GuestPassScreen
import com.example.electronichome.presentation.home.HomeScreen
import com.example.electronichome.presentation.management.ManagementApartmentsScreen
import com.example.electronichome.presentation.management.ManagementHomeScreen
import com.example.electronichome.presentation.management.ManagementRequestsScreen
import com.example.electronichome.presentation.meters.MetersScreen
import com.example.electronichome.presentation.requests.RequestsScreen
import com.example.electronichome.presentation.screens.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.electronichome.R

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int
)

val residentBottomNav = listOf(
    BottomNavItem(Screen.Announcements.route, "Объявления", R.drawable.ic_announcements),
    BottomNavItem(Screen.Home.route,          "Главная",    R.drawable.ic_apartments_b),
    BottomNavItem(Screen.Profile.route,       "Профиль",   R.drawable.ic_account)
)

val managementBottomNav = listOf(
    BottomNavItem(Screen.ManagementRequests.route,   "Заявки",   R.drawable.ic_requests_ps),
    BottomNavItem(Screen.ManagementHome.route,       "Главная",  R.drawable.ic_apartments_b),
    BottomNavItem(Screen.ManagementApartments.route, "Квартиры", R.drawable.ic_apartments)
)

private val bottomBarRoutes = (residentBottomNav + managementBottomNav).map { it.route }.toSet()

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()

    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        startDestination = when {
            currentUser == null -> Screen.Login.route
            else -> {
                val token = currentUser.getIdToken(true).await()
                val role  = token?.claims?.get("role") as? String
                if (role == "management") Screen.ManagementHome.route
                else Screen.Home.route
            }
        }
    }

    if (startDestination == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val currentBottomNav = when {
        currentRoute in managementBottomNav.map { it.route } -> managementBottomNav
        currentRoute in residentBottomNav.map { it.route }   -> residentBottomNav
        else -> null
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            currentBottomNav?.let { items ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 10.dp,
                        color = MaterialTheme.colorScheme.surface
                    ) {

                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp
                        ) {

                            items.forEach { item ->

                                NavigationBarItem(
                                    selected = currentRoute == item.route,

                                    onClick = {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.startDestinationId) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },

                                    icon = {
                                        Icon(
                                            painter = painterResource(id = item.icon),
                                            contentDescription = item.label,
                                            modifier = Modifier.size(24.dp),
                                            tint = Color.Unspecified
                                        )
                                    },

                                    label = {
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp
                                        )
                                    },

                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor =
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = startDestination!!,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                val state by authViewModel.state.collectAsState()

                LaunchedEffect(state.isSuccess, state.role) {
                    if (state.isSuccess && state.role != null) {
                        val dest = if (state.role == UserRole.MANAGEMENT)
                            Screen.ManagementHome.route
                        else
                            Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    onLoginSuccess       = {},
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel            = authViewModel
                )
            }

            composable(Screen.Register.route) {
                val state by authViewModel.state.collectAsState()

                LaunchedEffect(state.isSuccess) {
                    if (state.isSuccess) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                RegisterScreen(
                    onRegisterSuccess = {},
                    onNavigateBack    = { navController.popBackStack() },
                    viewModel         = authViewModel
                )
            }

            composable(Screen.Home.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                HomeScreen(navController = navController, viewModel = vm)
            }

            composable(Screen.Announcements.route) {
                AnnouncementsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToApartments = { navController.navigate(Screen.Apartments.route) },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToRequests = { navController.navigate(Screen.Requests.route) },
                )
            }

            composable(Screen.Apartments.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                ApartmentsScreen(
                    onAddApartment = { navController.navigate(Screen.AddApartment.route) },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel      = vm
                )
            }

            composable(Screen.AddApartment.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                AddApartmentScreen(
                    onSuccess      = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() },
                    viewModel      = vm
                )
            }

            composable(Screen.Meters.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                val state     by vm.state.collectAsState()
                val primaryId by vm.primaryId.collectAsState()
                val apartment = state.apartments.firstOrNull { it.id == primaryId }
                if (apartment != null) {
                    MetersScreen(
                        apartment      = apartment,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.Requests.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                val state     by vm.state.collectAsState()
                val primaryId by vm.primaryId.collectAsState()
                val apartment = state.apartments.firstOrNull { it.id == primaryId }
                if (apartment != null) {
                    RequestsScreen(
                        apartment      = apartment,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.GuestPass.route) {
                val vm = hiltViewModel<ApartmentsViewModel>(
                    remember { navController.getBackStackEntry(Screen.Home.route) }
                )
                val state     by vm.state.collectAsState()
                val primaryId by vm.primaryId.collectAsState()
                val apartment = state.apartments.firstOrNull { it.id == primaryId }
                if (apartment != null) {
                    GuestPassScreen(
                        apartment      = apartment,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }

            composable(Screen.ManagementHome.route) {
                ManagementHomeScreen(
                    navController = navController,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ManagementApartments.route) {
                ManagementApartmentsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ManagementRequests.route) {
                ManagementRequestsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}