package com.visionsystems.waterreminder.presenter.screens.editprofile

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.R
import com.visionsystems.waterreminder.domain.usecase.editprofile.EditProfileUseCase
import com.visionsystems.waterreminder.presenter.ui.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val direction: EditProfileContract.Direction,
    private val editProfileUseCase: EditProfileUseCase
) : ViewModel(), EditProfileContract.EditProfileViewModel {

    override val container = orbitContainer<EditProfileContract.EditProfileUiState, EditProfileContract.SideEffect>(
        EditProfileContract.EditProfileUiState()
    )

    init {
        intent {
            val data = editProfileUseCase.load()
            if (data == null) {
                direction.back()
                return@intent
            }
            reduce {
                state.copy(
                    isLoading = false,
                    fullName = data.profile.fullName,
                    gender = data.profile.gender,
                    age = data.profile.age.toString(),
                    weight = data.profile.weightKg.toString(),
                    activityLevel = data.profile.activityLevel,
                    photo = data.photo,
                    hasCustomPhoto = data.profile.photoPath != null
                )
            }
        }
    }

    override fun onEventDispatcher(event: EditProfileContract.EditProfileEvent) {
        when (event) {
            is EditProfileContract.EditProfileEvent.NameChanged -> intent {
                reduce { state.copy(fullName = event.value, nameError = false) }
            }

            is EditProfileContract.EditProfileEvent.GenderSelected -> intent { reduce { state.copy(gender = event.gender) } }

            is EditProfileContract.EditProfileEvent.AgeChanged -> intent {
                reduce { state.copy(age = event.value.digits(), ageError = false) }
            }

            is EditProfileContract.EditProfileEvent.WeightChanged -> intent {
                reduce { state.copy(weight = event.value.digits(), weightError = false) }
            }

            is EditProfileContract.EditProfileEvent.ActivitySelected -> intent { reduce { state.copy(activityLevel = event.level) } }

            EditProfileContract.EditProfileEvent.PhotoClicked -> intent {
                if (!state.isPhotoSaving) postSideEffect(EditProfileContract.SideEffect.OpenPhotoPicker)
            }

            is EditProfileContract.EditProfileEvent.PhotoPicked -> intent {
                reduce { state.copy(isPhotoSaving = true) }
                try {
                    editProfileUseCase.setPhoto(event.uri)
                        .onSuccess { photo -> reduce { state.copy(photo = photo ?: state.photo, hasCustomPhoto = photo != null) } }
                        .onFailure { postSideEffect(EditProfileContract.SideEffect.ShowMessage(UiText.Res(R.string.photo_failed))) }
                } finally {
                    reduce { state.copy(isPhotoSaving = false) }
                }
            }

            EditProfileContract.EditProfileEvent.RemovePhotoClicked -> intent {
                if (state.isPhotoSaving) return@intent
                reduce { state.copy(isPhotoSaving = true) }
                val fallback = editProfileUseCase.removePhoto()
                reduce { state.copy(photo = fallback, hasCustomPhoto = false, isPhotoSaving = false) }
            }

            EditProfileContract.EditProfileEvent.SaveClicked -> save()

            EditProfileContract.EditProfileEvent.BackClicked -> direction.back()
        }
    }

    private fun save() = intent {
        if (state.isSaving) return@intent
        val age = state.age.toIntOrNull()?.takeIf { it in EditProfileContract.AGE_RANGE }
        val weight = state.weight.toIntOrNull()?.takeIf { it in EditProfileContract.WEIGHT_RANGE }
        val nameError = state.fullName.isBlank()
        if (nameError || age == null || weight == null) {
            reduce { state.copy(nameError = nameError, ageError = age == null, weightError = weight == null) }
            return@intent
        }
        reduce { state.copy(isSaving = true) }
        editProfileUseCase.save(state.fullName, state.gender, age, weight, state.activityLevel)
        direction.back()
    }

    private fun String.digits(): String = filter { it.isDigit() }.take(EditProfileContract.MAX_DIGITS)
}
