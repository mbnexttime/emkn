package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.errorresponse.BeginChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.CommitChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.LoginErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.RegistrationErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.RevalidateRegistrationCredentialsErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.ValidateChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.ValidateEmailErrorResponseDto
import com.mcs.emkn.network.dto.request.BeginChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.CommitChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.LoginRequestDto
import com.mcs.emkn.network.dto.request.RegistrationRequestDto
import com.mcs.emkn.network.dto.request.RevalidateCredentialsDto
import com.mcs.emkn.network.dto.request.ValidateChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.ValidateEmailRequestDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenAndTimeDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
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
    ): NetworkResponse<ResponseWithTokenDto, ValidateChangePasswordErrorResponseDto>

    @POST("accounts/commit_change_password")
    suspend fun accountsCommitChangePassword(
        @Body request: CommitChangePasswordRequestDto
    ): NetworkResponse<Unit, CommitChangePasswordErrorResponseDto>

    @POST("accounts/revalidate_registration_credentials")
    suspend fun accountsRevalidateRegistrationCredentials(
        @Body request: RevalidateCredentialsDto,
    ): NetworkResponse<ResponseWithTokenAndTimeDto, RevalidateRegistrationCredentialsErrorResponseDto>
}