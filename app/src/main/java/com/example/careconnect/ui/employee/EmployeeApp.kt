package com.example.careconnect.ui.employee

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.careconnect.domain.model.OrganizationType
import com.example.careconnect.ui.employee.employeeProfile.EmployeeProfileScreen
import com.example.careconnect.ui.employee.home.HomeScreen
import com.example.careconnect.ui.employee.orderDetail.OrderDetailScreen
import com.example.careconnect.ui.employee.orders.OrdersScreen
import com.example.careconnect.ui.employee.organization.ManageOrganizationScreen
import com.example.careconnect.ui.employee.patientProfile.PatientProfileScreen
import com.example.careconnect.ui.employee.patients.PatientsScreen
import com.example.careconnect.ui.employee.prescriptions.PrescriptionsScreen
import com.example.careconnect.ui.employee.search.SearchScreen
import com.example.careconnect.ui.employee.settings.EditEmployeeProfileScreen
import com.example.careconnect.ui.employee.settings.EmployeeSettingsScreen

// Entspricht MainTabView aus iOS
@Composable
fun EmployeeApp(orgType: OrganizationType) {
    val navController = rememberNavController()

    // Markierter Tab richtet sich nach dem aktuellen Screen, auch nach "Zurück"
    val backStackEntry by navController.currentBackStackEntryAsState()
    val routeTab = EmployeeTabItem.entries.firstOrNull { tab ->
        backStackEntry?.destination?.hasRoute(tab.route::class) == true
    }
    var lastTab by rememberSaveable { mutableStateOf(EmployeeTabItem.HOME) }
    LaunchedEffect(routeTab) {
        routeTab?.let { lastTab = it }
    }
    val currentTab = routeTab ?: lastTab

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar {
                EmployeeTabItem.entries.forEach { tabItem ->
                    NavigationBarItem(
                        selected = currentTab == tabItem,
                        // Immer zur Startseite des Tabs, egal wie tief man gerade drin ist
                        onClick = {
                            navController.navigate(tabItem.route) {
                                popUpTo(EmployeeRoute.Home)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(tabItem.icon, tabItem.titleFor(orgType)) },
                        label = { Text(tabItem.titleFor(orgType), maxLines = 1) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EmployeeRoute.Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<EmployeeRoute.Home> {
                HomeScreen(
                    onSearchClick = { navController.navigate(EmployeeRoute.Search) },
                    onEmployeeClick = { employeeId ->
                        navController.navigate(EmployeeRoute.EmployeeProfile(employeeId))
                    },
                    onOrderClick = { orderId ->
                        navController.navigate(EmployeeRoute.OrderDetail(orderId))
                    }
                )
            }

            composable<EmployeeRoute.Patients> {
                PatientsScreen(
                    onPatientClick = { patientId ->
                        navController.navigate(EmployeeRoute.PatientProfile(patientId))
                    }
                )
            }

            composable<EmployeeRoute.Orders> {
                OrdersScreen(
                    onOrderClick = { orderId ->
                        navController.navigate(EmployeeRoute.OrderDetail(orderId))
                    }
                )
            }

            composable<EmployeeRoute.Prescriptions> {
                PrescriptionsScreen(orgType = orgType)
            }

            composable<EmployeeRoute.Settings> {
                EmployeeSettingsScreen(
                    onEditProfileClick = { navController.navigate(EmployeeRoute.EditProfile) },
                    onManageOrganizationClick = { navController.navigate(EmployeeRoute.ManageOrganization) }
                )
            }

            composable<EmployeeRoute.ManageOrganization> {
                ManageOrganizationScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.Search> {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onEmployeeClick = { navController.navigate(EmployeeRoute.EmployeeProfile(it)) },
                    onPatientClick = { navController.navigate(EmployeeRoute.PatientProfile(it)) },
                    onOrderClick = { navController.navigate(EmployeeRoute.OrderDetail(it)) }
                )
            }

            composable<EmployeeRoute.EditProfile> {
                EditEmployeeProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.PatientProfile> {
                PatientProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.EmployeeProfile> {
                EmployeeProfileScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable<EmployeeRoute.OrderDetail> {
                OrderDetailScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
