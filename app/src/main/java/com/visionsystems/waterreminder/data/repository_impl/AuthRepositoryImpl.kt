package com.visionsystems.waterreminder.data.repository_impl

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.visionsystems.waterreminder.data.mapper.toAuthException
import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.domain.exception.AuthError
import com.visionsystems.waterreminder.domain.exception.AuthException
import com.visionsystems.waterreminder.domain.module.AuthUserUiData
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    private val userState = MutableStateFlow(auth.currentUser?.toUIData())

    override val currentUser: StateFlow<AuthUserUiData?> = userState.asStateFlow()

    init {
        auth.addAuthStateListener { userState.value = it.currentUser?.toUIData() }
    }

    override suspend fun signUp(fullName: String, email: String, password: String): Result<AuthUserUiData> =
        runAuth {
            val user = auth.createUserWithEmailAndPassword(email.trim(), password).await().user
                ?: throw AuthException(AuthError.UNKNOWN)
            user.updateProfile(userProfileChangeRequest { displayName = fullName.trim() }).await()
            user.sendEmailVerification().await()
            refreshed(user)
        }

    override suspend fun signIn(email: String, password: String): Result<AuthUserUiData> =
        runAuth {
            val user = auth.signInWithEmailAndPassword(email.trim(), password).await().user
                ?: throw AuthException(AuthError.UNKNOWN)
            user.toUIData()
        }

    override suspend fun signInWithGoogle(idToken: String): Result<AuthUserUiData> =
        runAuth {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val user = auth.signInWithCredential(credential).await().user
                ?: throw AuthException(AuthError.UNKNOWN)
            user.toUIData()
        }

    override suspend fun sendEmailVerification(): Result<Unit> =
        runAuth { requireUser().sendEmailVerification().await() }

    override suspend fun reloadUser(): Result<AuthUserUiData> =
        runAuth { refreshed(requireUser()) }

    override suspend fun sendPasswordReset(email: String): Result<Unit> =
        runAuth { auth.sendPasswordResetEmail(email.trim()).await() }

    override suspend fun deleteAccount(): Result<Unit> =
        runAuth { requireUser().delete().await() }

    override fun signOut() {
        auth.signOut()
    }

    private suspend fun refreshed(user: FirebaseUser): AuthUserUiData {
        user.reload().await()
        val current = auth.currentUser ?: user
        return current.toUIData().also { userState.value = it }
    }

    private fun requireUser(): FirebaseUser = auth.currentUser ?: throw AuthException(AuthError.NOT_SIGNED_IN)

    private suspend fun <T> runAuth(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toAuthException())
        }
}
