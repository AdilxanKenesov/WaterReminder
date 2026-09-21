package com.visionsystems.waterreminder.presenter.screens.aboutyou

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import org.orbitmvi.orbit.OrbitContainerHost

interface AboutYouContract {

    interface AboutYouViewModel : OrbitContainerHost<AboutYouUiState, AboutYouUiState, SideEffect> {
        fun onEventDispatcher(event: AboutYouEvent)
    }

    sealed interface AboutYouEvent {
        data class GenderSelected(val gender: Gender) : AboutYouEvent
        data class AgeChanged(val value: Int) : AboutYouEvent
        data class WeightChanged(val value: Int) : AboutYouEvent
        data class ActivitySelected(val level: ActivityLevel) : AboutYouEvent
        data object ContinueClicked : AboutYouEvent
        data object BackClicked : AboutYouEvent
    }

    data class AboutYouUiState(
        val gender: Gender? = null,
        val age: Int = 30,
        val weightKg: Int = 70,
        val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
        val isSaving: Boolean = false
    ) {
        val canContinue: Boolean get() = gender != null && !isSaving
    }

    sealed interface SideEffect

    interface Direction {
        fun openSchedule()
        fun openSignIn()
    }

    companion object {
        val AGE_RANGE = 10..90
        val WEIGHT_RANGE = 30..200
    }
}
