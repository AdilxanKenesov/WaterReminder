package com.visionsystems.waterreminder.domain.usecase.verifyemail

import com.visionsystems.waterreminder.domain.module.AppStart
import kotlinx.coroutines.flow.Flow

interface VerifyEmailUseCase {
    fun observeOnline(): Flow<Boolean>
    fun currentEmail(): String
    suspend fun checkVerified(): Result<AppStart?>
    suspend fun resendLink(): Result<Unit>
    fun signOut()
}
