package com.visionsystems.waterreminder.presenter.ui.util

import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.exception.AuthError
import com.visionsystems.waterreminder.domain.exception.AuthException

fun Throwable.toUiText(): UiText = UiText.Res(
    when ((this as? AuthException)?.error) {
        AuthError.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
        AuthError.USER_NOT_FOUND -> R.string.error_user_not_found
        AuthError.EMAIL_ALREADY_IN_USE -> R.string.error_email_in_use
        AuthError.WEAK_PASSWORD -> R.string.error_weak_password
        AuthError.INVALID_EMAIL -> R.string.error_email_invalid
        AuthError.TOO_MANY_REQUESTS -> R.string.error_too_many_requests
        AuthError.NETWORK -> R.string.error_network
        AuthError.RECENT_LOGIN_REQUIRED -> R.string.error_recent_login
        AuthError.NOT_SIGNED_IN -> R.string.error_not_signed_in
        AuthError.UNKNOWN, null -> R.string.error_unknown
    }
)

fun String.isValidEmail(): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(trim()).matches()

const val MIN_PASSWORD_LENGTH = 8
