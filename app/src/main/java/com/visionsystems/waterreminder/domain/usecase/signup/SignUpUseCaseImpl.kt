package com.visionsystems.waterreminder.domain.usecase.signup

import com.visionsystems.waterreminder.domain.module.AppStart
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.NetworkRepository
import com.visionsystems.waterreminder.domain.usecase.common.StartResolver
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCaseImpl @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val authRepository: AuthRepository,
    private val startResolver: StartResolver
) : SignUpUseCase {

    override fun observeOnline(): Flow<Boolean> = networkRepository.isOnline

    override suspend fun signUp(fullName: String, email: String, password: String): Result<Unit> =
        authRepository.signUp(fullName, email, password).map { }

    override suspend fun signInWithGoogle(idToken: String): Result<AppStart> =
        authRepository.signInWithGoogle(idToken).map { startResolver.resolve() }
}
