package com.visionsystems.waterreminder.domain.usecase.signin

import com.visionsystems.waterreminder.domain.module.AppStart
import kotlinx.coroutines.flow.Flow

interface SignInUseCase {
    fun observeOnline(): Flow<Boolean>
    suspend fun signIn(email: String, password: String): Result<AppStart>
    suspend fun signInWithGoogle(idToken: String): Result<AppStart>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}
