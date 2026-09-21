package com.visionsystems.waterreminder.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cup_sizes",
    indices = [Index(value = ["uid", "amount_ml"], unique = true)]
)
data class CupSizeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "uid") val uid: String,
    @ColumnInfo(name = "amount_ml") val amountMl: Int,
    @ColumnInfo(name = "is_selected") val isSelected: Boolean = false
)
