package com.mcs.emkn.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mcs.emkn.database.entities.Credentials

@Database(entities = [Credentials::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun accountsDao(): AccountsDao
}