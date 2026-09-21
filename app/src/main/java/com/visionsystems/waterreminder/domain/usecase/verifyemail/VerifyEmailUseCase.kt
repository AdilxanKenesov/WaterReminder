package com.visionsystems.waterreminder.domain.usecase.verifyemail

import com.visionsystems.waterreminder.domain.module.AppStart

interface VerifyEmailUseCase {
    fun currentEmail(): String
    suspend fun checkVerified(): Result<AppStart?>
    suspend fun resendLink(): Result<Unit>
    fun signOut()
}
