package com.example.careconnect.domain.model

// Eigenes Profil, unabhängig von der Organisation
data class UserProfile(
    val uid: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val birthDate: Long? = null,
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val activeOrgId: String? = null
) {
    val name: String
        get() = "$firstName $lastName".trim()
}
