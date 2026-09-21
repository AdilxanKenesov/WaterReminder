package com.visionsystems.waterreminder.data.repository_impl

import com.visionsystems.waterreminder.data.mapper.toUIData
import com.visionsystems.waterreminder.data.source.local.room.dao.CupDao
import com.visionsystems.waterreminder.data.source.local.room.entity.CupSizeEntity
import com.visionsystems.waterreminder.domain.module.CupSizeUiData
import com.visionsystems.waterreminder.domain.repository.CupRepository
import com.visionsystems.waterreminder.domain.repository.CurrentUserProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CupRepositoryImpl @Inject constructor(
    private val cupDao: CupDao,
    private val currentUser: CurrentUserProvider
) : CupRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCups(): Flow<List<CupSizeUiData>> =
        currentUser.currentUid.flatMapLatest { uid ->
            flow {
                ensureDefaults(uid)
                emitAll(cupDao.observe(uid))
            }
        }.map { cups -> cups.map { it.toUIData() } }

    override suspend fun getSelectedCupMl(): Int {
        val uid = currentUser.currentUid.value
        ensureDefaults(uid)
        return cupDao.getSelected(uid)?.amountMl ?: DEFAULT_CUP_ML
    }

    override suspend fun selectCup(cupId: Long) {
        cupDao.select(currentUser.currentUid.value, cupId)
    }

    override suspend fun addCup(amountMl: Int) {
        val uid = currentUser.currentUid.value
        val id = cupDao.insert(CupSizeEntity(uid = uid, amountMl = amountMl.coerceIn(MIN_CUP_ML, MAX_CUP_ML)))
        if (id > 0) cupDao.select(uid, id)
    }

    override suspend fun deleteCup(cupId: Long) {
        cupDao.delete(currentUser.currentUid.value, cupId)
    }

    private suspend fun ensureDefaults(uid: String) {
        if (cupDao.count(uid) > 0) return
        cupDao.insertAll(DEFAULT_CUPS.map { ml -> CupSizeEntity(uid = uid, amountMl = ml, isSelected = ml == DEFAULT_CUP_ML) })
    }

    companion object {
        const val DEFAULT_CUP_ML = 250
        private const val MIN_CUP_ML = 50
        private const val MAX_CUP_ML = 2000
        private val DEFAULT_CUPS = listOf(100, 200, 250, 500)
    }
}
