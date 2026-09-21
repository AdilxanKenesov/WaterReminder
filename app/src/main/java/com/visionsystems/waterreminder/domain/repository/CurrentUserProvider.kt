package com.visionsystems.waterreminder.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface CurrentUserProvider {
    val currentUid: StateFlow<String>
}
