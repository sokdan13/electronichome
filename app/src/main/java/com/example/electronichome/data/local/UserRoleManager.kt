package com.example.electronichome.data.local

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

enum class UserRole { RESIDENT, MANAGEMENT }

@Singleton
class UserRoleManager @Inject constructor() {

    suspend fun getRole(): UserRole {
        val token = FirebaseAuth.getInstance()
            .currentUser
            ?.getIdToken(true)
            ?.await()
        val role = token?.claims?.get("role") as? String
        return when (role) {
            "management" -> UserRole.MANAGEMENT
            else         -> UserRole.RESIDENT
        }
    }

    fun isLoggedIn(): Boolean =
        FirebaseAuth.getInstance().currentUser != null
    
}