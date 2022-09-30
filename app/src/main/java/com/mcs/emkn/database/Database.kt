package com.mcs.emkn.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mcs.emkn.database.entities.ChangePasswordAttempt
import com.mcs.emkn.database.entities.ChangePasswordCommit
import com.mcs.emkn.database.entities.Credentials
import com.mcs.emkn.database.entities.SignUpAttempt

@Database(
    entities = [
        Credentials::class,
        SignUpAttempt::class,
        ChangePasswordAttempt::class,
        ChangePasswordCommit::class
    ],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract fun accountsDao(): AccountsDao
}