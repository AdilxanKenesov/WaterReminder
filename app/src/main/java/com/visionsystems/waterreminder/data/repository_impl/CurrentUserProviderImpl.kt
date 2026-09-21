package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.di.ApplicationScope
import com.visionsystems.waterreminder.domain.repository.AuthRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserProviderImpl @Inject constructor(
    authRepository: AuthRepository,
    @ApplicationScope scope: CoroutineScope
) : CurrentUserProvider {

    override val currentUid: StateFlow<String> = authRepository.currentUser
        .map { it?.uid ?: LOCAL_UID }
        .distinctUntilChanged()
        .stateIn(scope, SharingStarted.Eagerly, authRepository.currentUser.value?.uid ?: LOCAL_UID)

    companion object {
        const val LOCAL_UID = "local"
    }
}
