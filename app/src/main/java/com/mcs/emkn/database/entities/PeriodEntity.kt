package com.mcs.emkn.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mcs.emkn.database.entities.PeriodEntity.Companion.PERIODS_TABLE_NAME


@Entity(tableName = PERIODS_TABLE_NAME)
data class PeriodEntity(
    @PrimaryKey
    val id: Int,
    val text: String,
    var checked: Boolean,
    val isDefault: Boolean
) {
    companion object {
        const val PERIODS_TABLE_NAME = "periods"
    }
}