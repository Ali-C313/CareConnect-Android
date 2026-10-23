package com.example.careconnect.ui.auth

import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthRoute {
    @Serializable
    object Login : AuthRoute

    @Serializable
    object Register : AuthRoute
}
