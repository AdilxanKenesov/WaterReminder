package com.visionsystems.waterreminder.domain.repository

import kotlinx.coroutines.flow.StateFlow

interface NetworkRepository {
    val isOnline: StateFlow<Boolean>
}
