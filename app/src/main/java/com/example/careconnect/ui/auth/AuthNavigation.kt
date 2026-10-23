package com.example.careconnect.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AuthNavigation(
    isLoggedInWithoutProfile: Boolean,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoute.Login
    ) {
        composable<AuthRoute.Login> {
            LoginScreen(
                onRegisterClick = { navController.navigate(AuthRoute.Register) },
                isLoggedInWithoutProfile = isLoggedInWithoutProfile,
                onLogout = onLogout
            )
        }
        composable<AuthRoute.Register> {
            RegisterScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
