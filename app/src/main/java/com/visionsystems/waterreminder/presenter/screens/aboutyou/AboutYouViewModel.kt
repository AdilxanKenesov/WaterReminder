package com.visionsystems.waterreminder.presenter.screens.aboutyou

import androidx.lifecycle.ViewModel
import com.visionsystems.waterreminder.domain.usecase.aboutyou.AboutYouUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.viewmodel.orbitContainer
import javax.inject.Inject

@HiltViewModel
class AboutYouViewModel @Inject constructor(
    private val direction: AboutYouContract.Direction,
    private val aboutYouUseCase: AboutYouUseCase
) : ViewModel(), AboutYouContract.AboutYouViewModel {

    override val container = orbitContainer<AboutYouContract.AboutYouUiState, AboutYouContract.SideEffect>(AboutYouContract.AboutYouUiState())

    init {
        intent {
            val profile = aboutYouUseCase.loadProfile() ?: return@intent
            reduce {
                state.copy(
                    gender = profile.gender,
                    age = profile.age.coerceIn(AboutYouContract.AGE_RANGE),
                    weightKg = profile.weightKg.coerceIn(AboutYouContract.WEIGHT_RANGE),
                    activityLevel = profile.activityLevel
                )
            }
        }
    }

    override fun onEventDispatcher(event: AboutYouContract.AboutYouEvent) {
        when (event) {
            is AboutYouContract.AboutYouEvent.GenderSelected -> intent { reduce { state.copy(gender = event.gender) } }

            is AboutYouContract.AboutYouEvent.AgeChanged -> intent { reduce { state.copy(age = event.value) } }

            is AboutYouContract.AboutYouEvent.WeightChanged -> intent { reduce { state.copy(weightKg = event.value) } }

            is AboutYouContract.AboutYouEvent.ActivitySelected -> intent { reduce { state.copy(activityLevel = event.level) } }

            AboutYouContract.AboutYouEvent.ContinueClicked -> intent {
                val gender = state.gender ?: return@intent
                if (state.isSaving) return@intent
                reduce { state.copy(isSaving = true) }
                aboutYouUseCase.saveBasics(gender, state.age, state.weightKg, state.activityLevel)
                reduce { state.copy(isSaving = false) }
                direction.openSchedule()
            }

            AboutYouContract.AboutYouEvent.BackClicked -> {
                aboutYouUseCase.signOut()
                direction.openSignIn()
            }
        }
    }
}
