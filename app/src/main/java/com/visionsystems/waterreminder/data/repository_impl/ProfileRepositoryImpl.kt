package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toEntity
import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.file.AvatarStorage
import com.visionsystems.waterreminder.data.source.local.room.dao.AchievementDao
import com.visionsystems.waterreminder.data.source.local.room.dao.CupDao
import com.visionsystems.waterreminder.data.source.local.room.dao.DrinkDao
import com.visionsystems.waterreminder.data.source.local.room.dao.GoalDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ProfileDao
import com.visionsystems.waterreminder.data.source.local.room.dao.ReminderDao
import com.visionsystems.waterreminder.domain.module.UserProfileUiData
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import com.visionsystems.waterreminder.domain.repository.ProfileRepository
import com.visionsystems.waterreminder.domain.util.TimeProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: ProfileDao,
    private val drinkDao: DrinkDao,
    private val goalDao: GoalDao,
    private val cupDao: CupDao,
    private val reminderDao: ReminderDao,
    private val achievementDao: AchievementDao,
    private val avatarStorage: AvatarStorage,
    private val currentUser: CurrentUserProvider,
    private val time: TimeProvider
) : ProfileRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeProfile(): Flow<UserProfileUiData?> =
        currentUser.currentUid.flatMapLatest { uid -> profileDao.observe(uid) }.map { it?.toUIData() }

    override suspend fun getProfile(): UserProfileUiData? =
        profileDao.get(currentUser.currentUid.value)?.toUIData()

    override suspend fun saveProfile(profile: UserProfileUiData) {
        val uid = currentUser.currentUid.value
        val now = time.currentMillis()
        val createdAt = profileDao.get(uid)?.createdAt ?: now
        profileDao.upsert(profile.toEntity(uid = uid, createdAt = createdAt, updatedAt = now))
    }

    override suspend fun savePhoto(sourceUri: String): String? {
        val uid = currentUser.currentUid.value
        val entity = profileDao.get(uid) ?: return null
        val photo = avatarStorage.save(uid, sourceUri)
        profileDao.upsert(entity.copy(photoPath = photo, updatedAt = time.currentMillis()))
        avatarStorage.delete(entity.photoPath)
        return photo
    }

    override suspend fun removePhoto() {
        val uid = currentUser.currentUid.value
        val entity = profileDao.get(uid) ?: return
        profileDao.upsert(entity.copy(photoPath = null, updatedAt = time.currentMillis()))
        avatarStorage.delete(entity.photoPath)
    }

    override suspend fun deleteAllUserData() {
        val uid = currentUser.currentUid.value
        avatarStorage.delete(profileDao.get(uid)?.photoPath)
        drinkDao.deleteAll(uid)
        goalDao.deleteAll(uid)
        cupDao.deleteAll(uid)
        reminderDao.deleteAll(uid)
        achievementDao.deleteAll(uid)
        profileDao.delete(uid)
    }
}
