package com.visionsystems.waterreminder.presenter.screens.profile

import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost

interface ProfileContract {

    interface ProfileViewModel : OrbitContainerHost<ProfileUiState, ProfileUiState, SideEffect> {
        fun onEventDispatcher(event: ProfileEvent)
    }

    sealed interface ProfileEvent {
        data object EditClicked : ProfileEvent
        data object ChangeGoalClicked : ProfileEvent
        data object UnitsClicked : ProfileEvent
        data object LanguageClicked : ProfileEvent
        data class LanguageSelected(val tag: String) : ProfileEvent
        data object LanguageDismissed : ProfileEvent
        data object SignOutClicked : ProfileEvent
        data object DeleteClicked : ProfileEvent
        data object DeleteConfirmed : ProfileEvent
        data object DeleteDismissed : ProfileEvent
    }

    data class ProfileUiState(
        val isLoading: Boolean = true,
        val name: String = "",
        val email: String = "",
        val photo: String? = null,
        val weightKg: Int = 0,
        val age: Int = 0,
        val goalMl: Int = 0,
        val isRecommendedGoal: Boolean = true,
        val unit: WaterUnit = WaterUnit.ML,
        val showLanguageDialog: Boolean = false,
        val showDeleteDialog: Boolean = false,
        val isDeleting: Boolean = false,
        val isOffline: Boolean = false
    )

    sealed interface SideEffect {
        data class ShowMessage(val message: UiText) : SideEffect
        data class ApplyLanguage(val tag: String) : SideEffect
    }

    interface Direction {
        fun openEditProfile()
        fun openSetGoal()
        fun openSignIn()
    }

    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_UZBEK = "uz"
    }
}
