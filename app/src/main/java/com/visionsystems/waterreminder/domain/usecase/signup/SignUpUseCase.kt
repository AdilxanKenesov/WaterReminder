package com.visionsystems.waterreminder.domain.usecase.signup

import com.visionsystems.waterreminder.domain.module.AppStart

interface SignUpUseCase {
    suspend fun signUp(fullName: String, email: String, password: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<AppStart>
}
