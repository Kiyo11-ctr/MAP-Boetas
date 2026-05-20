package com.valentinesgarage.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.valentinesgarage.ui.checkin.CheckInScreen
import com.valentinesgarage.ui.dashboard.DashboardScreen
import com.valentinesgarage.ui.employees.EmployeesScreen
import com.valentinesgarage.ui.reports.ReportsScreen
import com.valentinesgarage.ui.service.ServiceBoardScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard    : Screen("dashboard",  "Dashboard", Icons.Default.Dashboard)
    object CheckIn      : Screen("checkin",    "Check-In",  Icons.Default.LocalShipping)
    object ServiceBoard : Screen("service",    "Service",   Icons.Default.Checklist)
    object Reports      : Screen("reports",    "Reports",   Icons.Default.BarChart)
    object Employees    : Screen("employees",  "Employees", Icons.Default.People)
}

private val navItems = listOf(
    Screen.Dashboard, Screen.CheckIn, Screen.ServiceBoard,
    Screen.Reports, Screen.Employees
)

@Composable
fun GarageNavHost() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                val entry by navController.currentBackStackEntryAsState()
                val current = entry?.destination
                navItems.forEach { screen ->
                    NavigationBarItem(
                        icon     = { Icon(screen.icon, contentDescription = screen.label) },
                        label    = { Text(screen.label) },
                        selected = current?.hierarchy?.any { it.route == screen.route } == true,
                        onClick  = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController  = navController,
            startDestination = Screen.Dashboard.route,
            modifier       = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route)    { DashboardScreen() }
            composable(Screen.CheckIn.route)      { CheckInScreen() }
            composable(Screen.ServiceBoard.route) { ServiceBoardScreen() }
            composable(Screen.Reports.route)      { ReportsScreen() }
            composable(Screen.Employees.route)    { EmployeesScreen() }
        }
    }
}
