package com.visionsystems.waterreminder.domain.exception

enum class AuthError {
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    EMAIL_ALREADY_IN_USE,
    WEAK_PASSWORD,
    INVALID_EMAIL,
    TOO_MANY_REQUESTS,
    NETWORK,
    RECENT_LOGIN_REQUIRED,
    NOT_SIGNED_IN,
    UNKNOWN
}

class AuthException(val error: AuthError, cause: Throwable? = null) : Exception(error.name, cause)
