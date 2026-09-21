package com.visionsystems.waterreminder.domain.usecase.signin

import com.visionsystems.waterreminder.domain.module.AppStart

interface SignInUseCase {
    suspend fun signIn(email: String, password: String): Result<AppStart>
    suspend fun signInWithGoogle(idToken: String): Result<AppStart>
    suspend fun sendPasswordReset(email: String): Result<Unit>
}
