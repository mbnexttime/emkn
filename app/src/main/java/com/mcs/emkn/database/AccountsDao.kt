package com.mcs.emkn.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.ABORT
import androidx.room.Query
import com.mcs.emkn.database.entities.Credentials
import com.mcs.emkn.database.entities.Credentials.Companion.CREDENTIALS_TABLE_NAME
import com.mcs.emkn.database.entities.SignUpAttempt
import com.mcs.emkn.database.entities.SignUpAttempt.Companion.SIGN_UP_ATTEMPT_TABLE_NAME

@Dao
interface AccountsDao {
    @Query("SELECT * FROM $CREDENTIALS_TABLE_NAME")
    fun getCredentials(): List<Credentials>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putCredentials(credentials: Credentials)

    @Query("SELECT * FROM $SIGN_UP_ATTEMPT_TABLE_NAME")
    fun getSignUpAttempts(): List<SignUpAttempt>

    @Query("DELETE * FROM $SIGN_UP_ATTEMPT_TABLE_NAME")
    fun deleteSignUpAttempts()

    @Insert(onConflict = ABORT)
    fun putSignUpAttempt(signUpAttempt: SignUpAttempt)
}