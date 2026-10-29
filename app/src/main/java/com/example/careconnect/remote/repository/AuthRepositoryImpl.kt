package com.example.careconnect.remote.repository

import com.example.careconnect.R
import com.example.careconnect.domain.repository.AuthRepository
import com.example.careconnect.util.ResourceHelper
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth
) : AuthRepository {

    // uid vom eingeloggten Nutzer, null nach dem Logout
    override val authState: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val currentUserEmail: String?
        get() = auth.currentUser?.email

    override suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        auth.signInWithEmailAndPassword(email.trim(), password).await()
    }

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        result.user?.uid ?: throw Exception(ResourceHelper.getString(R.string.registrierung_fehlgeschlagen))
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        auth.signOut()
    }

    override suspend fun changePassword(currentPassword: String?, newPassword: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw Exception(ResourceHelper.getString(R.string.kein_eingeloggter_benutzer_gefunden))
        // Bei Bedarf neu anmelden, sonst lehnt Firebase die Änderung ab
        if (!currentPassword.isNullOrEmpty()) {
            val credential = EmailAuthProvider.getCredential(user.email ?: "", currentPassword)
            user.reauthenticate(credential).await()
        }
        user.updatePassword(newPassword).await()
    }
}
