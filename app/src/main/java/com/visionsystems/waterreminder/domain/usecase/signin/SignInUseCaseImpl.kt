package com.visionsystems.waterreminder.domain.usecase.signin

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import javax.inject.Inject

class SignInUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val startResolver: StartResolver
) : SignInUseCase {

    override suspend fun signIn(email: String, password: String): Result<AppStart> =
        authRepository.signIn(email, password).map { startResolver.resolve() }

    override suspend fun signInWithGoogle(idToken: String): Result<AppStart> =
        authRepository.signInWithGoogle(idToken).map { startResolver.resolve() }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        authRepository.sendPasswordReset(email)
}
