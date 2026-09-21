package com.visionsystems.waterreminder.domain.usecase.verifyemail

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import javax.inject.Inject

class VerifyEmailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val startResolver: StartResolver
) : VerifyEmailUseCase {

    override fun currentEmail(): String = authRepository.currentUser.value?.email.orEmpty()

    override suspend fun checkVerified(): Result<AppStart?> =
        authRepository.reloadUser().map { user ->
            if (user.isEmailVerified) startResolver.resolve() else null
        }

    override suspend fun resendLink(): Result<Unit> = authRepository.sendEmailVerification()

    override fun signOut() {
        authRepository.signOut()
    }
}
