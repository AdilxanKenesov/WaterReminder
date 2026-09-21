package com.visionsystems.waterreminder.presenter.ui.util

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

sealed interface UiText {
    data class Res(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
    data class Plural(@param:PluralsRes val id: Int, val count: Int, val args: List<Any> = emptyList()) : UiText
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.Res -> context.getString(id, *resolve(args, context))
        is UiText.Plural -> context.resources.getQuantityString(id, count, *resolve(args, context))
    }
}

@Composable
fun UiText.asString(): String = asString(LocalContext.current)

private fun resolve(args: List<Any>, context: Context): Array<Any> =
    args.map { if (it is UiText) it.asString(context) else it }.toTypedArray()
