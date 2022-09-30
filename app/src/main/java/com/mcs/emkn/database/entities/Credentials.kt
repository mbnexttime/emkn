package com.mcs.emkn.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mcs.emkn.database.entities.Credentials.Companion.CREDENTIALS_TABLE_NAME

@Entity(tableName = CREDENTIALS_TABLE_NAME)
data class Credentials(
    @PrimaryKey
    val login: String,
    val password: String,
) {
    companion object {
        const val CREDENTIALS_TABLE_NAME = "credentials"
    }
}