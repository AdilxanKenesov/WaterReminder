package com.visionsystems.waterreminder.presenter.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.visionsystems.waterreminder.domain.usecase.splash.SplashUseCase
import com.visionsystems.waterreminder.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    splashUseCase: SplashUseCase
) : ViewModel() {

    val startRoute: StateFlow<NavKey?> = flow { emit(splashUseCase.resolveStart().toRoute()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
