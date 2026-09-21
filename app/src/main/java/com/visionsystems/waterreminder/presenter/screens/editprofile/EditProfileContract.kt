package com.visionsystems.waterreminder.presenter.screens.editprofile

import com.visionsystems.waterreminder.domain.module.ActivityLevel
import com.visionsystems.waterreminder.domain.module.Gender
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import org.orbitmvi.orbit.OrbitContainerHost

interface EditProfileContract {

    interface EditProfileViewModel : OrbitContainerHost<EditProfileUiState, EditProfileUiState, SideEffect> {
        fun onEventDispatcher(event: EditProfileEvent)
    }

    sealed interface EditProfileEvent {
        data class NameChanged(val value: String) : EditProfileEvent
        data class GenderSelected(val gender: Gender) : EditProfileEvent
        data class AgeChanged(val value: String) : EditProfileEvent
        data class WeightChanged(val value: String) : EditProfileEvent
        data class ActivitySelected(val level: ActivityLevel) : EditProfileEvent
        data object PhotoClicked : EditProfileEvent
        data class PhotoPicked(val uri: String) : EditProfileEvent
        data object RemovePhotoClicked : EditProfileEvent
        data object SaveClicked : EditProfileEvent
        data object BackClicked : EditProfileEvent
    }

    data class EditProfileUiState(
        val isLoading: Boolean = true,
        val fullName: String = "",
        val gender: Gender = Gender.OTHER,
        val age: String = "",
        val weight: String = "",
        val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
        val photo: String? = null,
        val hasCustomPhoto: Boolean = false,
        val isPhotoSaving: Boolean = false,
        val nameError: Boolean = false,
        val ageError: Boolean = false,
        val weightError: Boolean = false,
        val isSaving: Boolean = false
    )

    sealed interface SideEffect {
        data object OpenPhotoPicker : SideEffect
        data class ShowMessage(val message: UiText) : SideEffect
    }

    interface Direction {
        fun back()
    }

    companion object {
        val AGE_RANGE = 5..100
        val WEIGHT_RANGE = 20..250
        const val MAX_DIGITS = 3
    }
}
