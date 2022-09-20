package com.mcs.emkn.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mcs.emkn.database.entities.Credentials

@Dao
interface AccountsDao {
    @Query("SELECT * FROM credentials")
    fun getAll(): List<Credentials>
    
    @Insert
    fun putCredentials(credentials: Credentials)
}