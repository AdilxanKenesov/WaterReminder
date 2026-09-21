package com.visionsystems.waterreminder.data.mapper

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.visionsystems.waterreminder.domain.exception.AuthError
import com.visionsystems.waterreminder.domain.exception.AuthException
import com.visionsystems.waterreminder.domain.module.AuthUserUiData

private const val ERROR_INVALID_EMAIL = "ERROR_INVALID_EMAIL"
private const val GOOGLE_PHOTO_SMALL = "s96-c"
private const val GOOGLE_PHOTO_LARGE = "s400-c"

fun FirebaseUser.toUIData(): AuthUserUiData =
    AuthUserUiData(
        uid = uid,
        email = email.orEmpty(),
        displayName = displayName.orEmpty(),
        isEmailVerified = isEmailVerified,
        isGoogleAccount = providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID },
        photoUrl = photoUrl?.toString()?.replace(GOOGLE_PHOTO_SMALL, GOOGLE_PHOTO_LARGE)
    )

fun Throwable.toAuthException(): AuthException = when (this) {
    is AuthException -> this
    is FirebaseAuthWeakPasswordException -> AuthException(AuthError.WEAK_PASSWORD, this)
    is FirebaseAuthInvalidCredentialsException ->
        if (errorCode == ERROR_INVALID_EMAIL) AuthException(AuthError.INVALID_EMAIL, this)
        else AuthException(AuthError.INVALID_CREDENTIALS, this)
    is FirebaseAuthInvalidUserException -> AuthException(AuthError.USER_NOT_FOUND, this)
    is FirebaseAuthUserCollisionException -> AuthException(AuthError.EMAIL_ALREADY_IN_USE, this)
    is FirebaseAuthRecentLoginRequiredException -> AuthException(AuthError.RECENT_LOGIN_REQUIRED, this)
    is FirebaseTooManyRequestsException -> AuthException(AuthError.TOO_MANY_REQUESTS, this)
    is FirebaseNetworkException -> AuthException(AuthError.NETWORK, this)
    else -> AuthException(AuthError.UNKNOWN, this)
}
