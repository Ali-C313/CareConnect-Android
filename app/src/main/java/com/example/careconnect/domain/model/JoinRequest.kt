package com.example.careconnect.domain.model

// Patient fragt bei einer Organisation an, statt einen Code zu haben
data class JoinRequest(
    val orgId: String,
    val orgName: String,
    val orgType: OrganizationType,
    val uid: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val address: String = "",
    val city: String = "",
    val phone: String = "",
    val birthDate: Long? = null,
    val profileImageUrl: String = "",
    val status: JoinRequestStatus = JoinRequestStatus.PENDING,
    val createdAt: Long? = null
) {
    val name: String
        get() = "$firstName $lastName".trim().ifEmpty { email }
}
