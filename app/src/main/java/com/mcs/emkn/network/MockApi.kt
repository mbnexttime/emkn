package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.error.NetError
import com.mcs.emkn.network.dto.errorresponse.*
import com.mcs.emkn.network.dto.request.BeginChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.CommitChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.LoginRequestDto
import com.mcs.emkn.network.dto.request.RegistrationRequestDto
import com.mcs.emkn.network.dto.request.RevalidateCredentialsDto
import com.mcs.emkn.network.dto.request.ValidateChangePasswordRequestDto
import com.mcs.emkn.network.dto.request.ValidateEmailRequestDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenAndTimeDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
import com.mcs.emkn.network.dto.response.TokenAndTimeDto
import kotlinx.coroutines.delay
import retrofit2.Response

class MockApi : Api {
    override suspend fun accountsRegister(request: RegistrationRequestDto): NetworkResponse<ResponseWithTokenAndTimeDto, RegistrationErrorResponseDto> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            ResponseWithTokenAndTimeDto(TokenAndTimeDto( "5", "hdjshd")),
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            RegistrationErrorResponseDto(
                RegistrationErrorResponseErrorsDto(
                    NetError("1"), null, null
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun validateEmail(request: ValidateEmailRequestDto): NetworkResponse<Unit, ValidateEmailErrorResponseDto> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            Unit,
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            ValidateEmailErrorResponseDto(
                ValidateEmailErrorResponseErrorsDto(
                    NetError("1"), NetError("2")
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun accountsLogin(request: LoginRequestDto): NetworkResponse<Unit, LoginErrorResponseDto> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(Unit, Response.success(200, "OK"))
        else NetworkResponse.ServerError(
            LoginErrorResponseDto(
                LoginErrorResponseErrorsDto(
                    NetError("1")
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun accountsBeginChangePassword(request: BeginChangePasswordRequestDto): NetworkResponse<ResponseWithTokenAndTimeDto, BeginChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsValidateChangePassword(request: ValidateChangePasswordRequestDto): NetworkResponse<ResponseWithTokenDto, ValidateChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsCommitChangePassword(request: CommitChangePasswordRequestDto): NetworkResponse<Unit, CommitChangePasswordErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsRevalidateCredentials(request: RevalidateCredentialsDto): NetworkResponse<ResponseWithTokenAndTimeDto, Unit> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            ResponseWithTokenAndTimeDto(TokenAndTimeDto( "5", "hdjshd")),
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            Unit
            , response = Response.success(200, "BAD")
        )
    }
}