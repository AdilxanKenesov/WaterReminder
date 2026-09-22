package com.visionsystems.waterreminder.domain.usecase.signup

import com.visionsystems.waterreminder.domain.module.AppStart
import kotlinx.coroutines.flow.Flow

interface SignUpUseCase {
    fun observeOnline(): Flow<Boolean>
    suspend fun signUp(fullName: String, email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<AppStart>
}
