package com.visionsystems.waterreminder.data.source.local.pref

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedManager @Inject constructor(
    private val prefs: SharedPreferences
) {

    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_DONE, value) }

    var unit: String?
        get() = prefs.getString(KEY_UNIT, null)
        set(value) = prefs.edit { putString(KEY_UNIT, value) }

    var language: String?
        get() = prefs.getString(KEY_LANGUAGE, null)
        set(value) = prefs.edit { putString(KEY_LANGUAGE, value) }

    var reviewRequested: Boolean
        get() = prefs.getBoolean(KEY_REVIEW_REQUESTED, false)
        set(value) = prefs.edit { putBoolean(KEY_REVIEW_REQUESTED, value) }

    fun getNotifiedDay(type: String, uid: String): Int = prefs.getInt(notifiedKey(type, uid), 0)

    fun setNotifiedDay(type: String, uid: String, dayKey: Int) {
        prefs.edit { putInt(notifiedKey(type, uid), dayKey) }
    }

    private fun notifiedKey(type: String, uid: String) = "notified_${type}_$uid"

    companion object {
        const val NOTIFIED_GOAL = "goal"
        const val NOTIFIED_STREAK = "streak"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_UNIT = "unit"
        private const val KEY_REVIEW_REQUESTED = "review_requested"
        private const val KEY_LANGUAGE = "language"
    }
}
