package com.mcs.emkn.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Credentials(
    @PrimaryKey
    val login: String,
    @ColumnInfo(name = "password") val password: String,
)