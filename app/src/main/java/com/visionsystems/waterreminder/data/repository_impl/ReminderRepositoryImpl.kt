package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toEntity
import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.ReminderDao
import com.visionsystems.waterreminder.domain.module.ReminderConfigUiData
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.ReminderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    private val currentUser: CurrentUserProvider
) : ReminderRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeConfig(): Flow<ReminderConfigUiData> =
        currentUser.currentUid.flatMapLatest { uid -> reminderDao.observe(uid) }
            .map { it?.toUIData() ?: ReminderConfigUiData() }

    override suspend fun getConfig(): ReminderConfigUiData =
        reminderDao.get(currentUser.currentUid.value)?.toUIData() ?: ReminderConfigUiData()

    override suspend fun updateConfig(config: ReminderConfigUiData) {
        reminderDao.upsert(config.toEntity(currentUser.currentUid.value))
    }
}
