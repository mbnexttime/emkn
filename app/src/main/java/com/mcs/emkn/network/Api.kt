package com.mcs.emkn.network

import com.mcs.emkn.network.dto.request.*
import com.mcs.emkn.network.dto.response.ResponseEmptyDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenAndTimeDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("accounts/register")
    suspend fun accountsRegister(
        @Body request: RegistrationRequestDto
    ): AuthResponse<ResponseWithTokenAndTimeDto>

    @POST("accounts/validate_email")
    suspend fun validateEmail(
        @Body request: ValidateEmailRequestDto
    ): AuthResponse<ResponseEmptyDto>

    @POST("accounts/login")
    suspend fun accountsLogin(
        @Body request: LoginRequestDto
    ): AuthResponse<ResponseEmptyDto>

    @POST("accounts/change_password")
    suspend fun accountsChangePassword(
        @Body request: ChangePasswordRequestDto
    ): AuthResponse<ResponseWithTokenAndTimeDto>

    @POST("accounts/validate_change_password")
    suspend fun accountsValidateChangePassword(
        @Body request: ValidateChangePasswordRequestDto
    ) : AuthResponse<ResponseWithTokenDto>

    @POST("accounts/commit_change_password")
    suspend fun accountsCommitChangePassword(
        @Body request: CommitChangePasswordRequestDto
    ) : AuthResponse<ResponseEmptyDto>
}