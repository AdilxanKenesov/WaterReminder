package com.visionsystems.waterreminder.presenter.ui.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.visionsystems.waterreminder.R

sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult
    data object Cancelled : GoogleSignInResult
    data class Failure(val message: UiText) : GoogleSignInResult
}

@SuppressLint("DiscouragedApi")
suspend fun Context.requestGoogleIdToken(): GoogleSignInResult {
    val clientIdRes = resources.getIdentifier("default_web_client_id", "string", packageName)
    if (clientIdRes == 0) return GoogleSignInResult.Failure(UiText.Res(R.string.error_google_not_configured))
    val option = GetSignInWithGoogleOption.Builder(getString(clientIdRes)).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
    return try {
        val credential = CredentialManager.create(this).getCredential(this, request).credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            GoogleSignInResult.Success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
        } else {
            GoogleSignInResult.Failure(UiText.Res(R.string.error_google_failed))
        }
    } catch (e: GetCredentialCancellationException) {
        GoogleSignInResult.Cancelled
    } catch (e: NoCredentialException) {
        GoogleSignInResult.Failure(UiText.Res(R.string.error_google_no_account))
    } catch (e: GetCredentialException) {
        GoogleSignInResult.Failure(UiText.Res(R.string.error_google_failed))
    }
}
