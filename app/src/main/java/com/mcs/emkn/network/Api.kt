package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.errorresponse.*
import com.mcs.emkn.network.dto.request.*
import com.mcs.emkn.network.dto.response.ResponseWithTokenAndTimeDto
import com.mcs.emkn.network.dto.response.TokenDto
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("accounts/register")
    suspend fun accountsRegister(
        @Body request: RegistrationRequestDto
    ): NetworkResponse<ResponseWithTokenAndTimeDto, RegistrationErrorResponseDto>

    @POST("accounts/validate_email")
    suspend fun validateEmail(
        @Body request: ValidateEmailRequestDto
    ): NetworkResponse<Unit, ValidateEmailErrorResponseDto>

    @POST("accounts/login")
    suspend fun accountsLogin(
        @Body request: LoginRequestDto
    ): NetworkResponse<Unit, LoginErrorResponseDto>

    @POST("accounts/begin_change_password")
    suspend fun accountsBeginChangePassword(
        @Body request: BeginChangePasswordRequestDto
    ): NetworkResponse<ResponseWithTokenAndTimeDto, BeginChangePasswordErrorResponseDto>

    @POST("accounts/validate_change_password")
    suspend fun accountsValidateChangePassword(
        @Body request: ValidateChangePasswordRequestDto
    ): NetworkResponse<TokenDto, ValidateChangePasswordErrorResponseDto>

    @POST("accounts/commit_change_password")
    suspend fun accountsCommitChangePassword(
        @Body request: CommitChangePasswordRequestDto
    ): NetworkResponse<Unit, CommitChangePasswordErrorResponseDto>

    @POST("accounts/revalidate_registration_credentials")
    suspend fun accountsRevalidateRegistrationCredentials(
        @Body request: RevalidateCredentialsDto,
    ): NetworkResponse<ResponseWithTokenAndTimeDto, RevalidateRegistrationCredentialsErrorResponseDto>

    @POST("accounts/revalidate_change_password_credentials")
    suspend fun accountsRevalidateChangePasswordCredentials(
        @Body request: RevalidateCredentialsDto,
    ): NetworkResponse<Unit, RevalidateRegistrationCredentialsErrorResponseDto>
}