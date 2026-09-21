package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "drink_entries",
    indices = [Index(value = ["uid", "day_key"])]
)
data class DrinkEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "amount_ml") val amountMl: Int,
    @ColumnInfo(name = "drink_type") val drinkType: String = DRINK_TYPE_WATER,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "day_key") val dayKey: Int
) {
    companion object {
        const val DRINK_TYPE_WATER = "WATER"
    }
}
