package com.visionsystems.waterreminder.presenter.screens.profile

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.module.WaterUnit
import com.visionsystems.waterreminder.domain.usecase.profile.ProfileUseCase
import com.visionsystems.waterreminder.presenter.ui.util.toUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val direction: ProfileContract.Direction,
    private val profileUseCase: ProfileUseCase
) : ViewModel(), ProfileContract.ProfileViewModel {

    override val container = orbitContainer<ProfileContract.ProfileUiState, ProfileContract.SideEffect>(ProfileContract.ProfileUiState())

    init {
        intent {
            profileUseCase.observeProfile().collect { data ->
                reduce {
                    state.copy(
                        isLoading = false,
                        name = data.displayName,
                        email = data.email,
                        photo = data.photo,
                        weightKg = data.profile?.weightKg ?: 0,
                        age = data.profile?.age ?: 0,
                        goalMl = data.goalMl,
                        isRecommendedGoal = data.goalMl == data.recommendedMl,
                        unit = data.unit
                    )
                }
            }
        }
    }

    override fun onEventDispatcher(event: ProfileContract.ProfileEvent) {
        when (event) {
            ProfileContract.ProfileEvent.EditClicked -> direction.openEditProfile()

            ProfileContract.ProfileEvent.ChangeGoalClicked -> direction.openSetGoal()

            ProfileContract.ProfileEvent.UnitsClicked -> intent {
                profileUseCase.setUnit(if (state.unit == WaterUnit.ML) WaterUnit.OZ else WaterUnit.ML)
            }

            ProfileContract.ProfileEvent.LanguageClicked -> intent { reduce { state.copy(showLanguageDialog = true) } }

            ProfileContract.ProfileEvent.LanguageDismissed -> intent { reduce { state.copy(showLanguageDialog = false) } }

            is ProfileContract.ProfileEvent.LanguageSelected -> intent {
                reduce { state.copy(showLanguageDialog = false) }
                profileUseCase.setLanguage(event.tag)
                postSideEffect(ProfileContract.SideEffect.ApplyLanguage(event.tag))
            }

            ProfileContract.ProfileEvent.SignOutClicked -> {
                profileUseCase.signOut()
                direction.openSignIn()
            }

            ProfileContract.ProfileEvent.DeleteClicked -> intent { reduce { state.copy(showDeleteDialog = true) } }

            ProfileContract.ProfileEvent.DeleteDismissed -> intent {
                if (!state.isDeleting) reduce { state.copy(showDeleteDialog = false) }
            }

            ProfileContract.ProfileEvent.DeleteConfirmed -> intent {
                if (state.isDeleting) return@intent
                reduce { state.copy(isDeleting = true) }
                profileUseCase.deleteAccount()
                    .onSuccess { direction.openSignIn() }
                    .onFailure {
                        reduce { state.copy(isDeleting = false, showDeleteDialog = false) }
                        postSideEffect(ProfileContract.SideEffect.ShowMessage(it.toUiText()))
                    }
            }
        }
    }
}
