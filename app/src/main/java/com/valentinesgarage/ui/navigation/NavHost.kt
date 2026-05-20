package com.valentinesgarage.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.valentinesgarage.ui.auth.AuthViewModel
import com.valentinesgarage.ui.auth.LoginScreen
import com.valentinesgarage.ui.auth.SignUpScreen

/**
 * Navigation destinations for the app.
 * Using sealed class to make routes type-safe — prevents typo bugs.
 */
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Login       : Screen("login",      "Login",      Icons.Default.Login)
    object SignUp      : Screen("signup",     "Sign Up",     Icons.Default.PersonAdd)
    object Dashboard   : Screen("dashboard",  "Dashboard",  Icons.Default.Dashboard)
    object CheckIn     : Screen("checkin",    "Check-In",   Icons.Default.LocalShipping)
    object ServiceBoard: Screen("service",    "Service",    Icons.Default.Checklist)
    object Reports     : Screen("reports",    "Reports",    Icons.Default.BarChart)
    object Employees   : Screen("employees",  "Employees",  Icons.Default.People)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.CheckIn,
    Screen.ServiceBoard,
    Screen.Reports,
    Screen.Employees
)

/**
 * Root composable that owns the NavController and renders the
 * bottom navigation bar + each screen destination.
 */
@Composable
fun GarageNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.uiState.collectAsState()

    val showBottomBar = authState.isAuthenticated &&
            listOf(Screen.Dashboard.route, Screen.CheckIn.route, Screen.ServiceBoard.route, Screen.Reports.route, Screen.Employees.route)
                .contains(navController.currentBackStackEntryAsState().value?.destination?.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authState.isAuthenticated) Screen.Dashboard.route else Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Dashboard.route)    { com.valentinesgarage.ui.dashboard.DashboardScreen() }
            composable(Screen.CheckIn.route)      { com.valentinesgarage.ui.checkin.CheckInScreen() }
            composable(Screen.ServiceBoard.route) { com.valentinesgarage.ui.service.ServiceBoardScreen() }
            composable(Screen.Reports.route)      { com.valentinesgarage.ui.reports.ReportsScreen() }
            composable(Screen.Employees.route)    { com.valentinesgarage.ui.employees.EmployeesScreen() }
        }

        // Auth Guard
        LaunchedEffect(authState.isAuthenticated) {
            if (!authState.isAuthenticated) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            } else if (navController.currentDestination?.route == Screen.Login.route ||
                navController.currentDestination?.route == Screen.SignUp.route) {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
    }
}
