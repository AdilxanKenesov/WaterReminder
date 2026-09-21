package com.visionsystems.waterreminder.domain.repository

import com.visionsystems.waterreminder.domain.module.AuthUserUiData
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<AuthUserUiData?>
    suspend fun signUp(fullName: String, email: String, password: String): Result<AuthUserUiData>
    suspend fun signIn(email: String, password: String): Result<AuthUserUiData>
    suspend fun signInWithGoogle(idToken: String): Result<AuthUserUiData>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun reloadUser(): Result<AuthUserUiData>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    fun signOut()
}
