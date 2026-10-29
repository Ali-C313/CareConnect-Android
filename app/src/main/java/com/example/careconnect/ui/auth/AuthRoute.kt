package com.example.careconnect.ui.auth

import kotlinx.serialization.Serializable

// Routen für Login und Registrierung
@Serializable
sealed interface AuthRoute {
    @Serializable
    object Login : AuthRoute

    @Serializable
    object Register : AuthRoute
}
