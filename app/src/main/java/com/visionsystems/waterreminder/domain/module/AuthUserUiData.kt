package com.visionsystems.waterreminder.domain.module

data class AuthUserUiData(
    val uid: String,
    val email: String,
    val displayName: String,
    val isEmailVerified: Boolean,
    val isGoogleAccount: Boolean,
    val photoUrl: String? = null
)
