package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.error.NetError
import com.mcs.emkn.network.dto.errorresponse.BeginChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.BeginChangePasswordErrorResponseErrorsDto
import com.mcs.emkn.network.dto.errorresponse.CommitChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.CommitChangePasswordErrorResponseErrorsDto
import com.mcs.emkn.network.dto.errorresponse.LoginErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.LoginErrorResponseErrorsDto
import com.mcs.emkn.network.dto.errorresponse.RegistrationErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.RegistrationErrorResponseErrorsDto
import com.mcs.emkn.network.dto.errorresponse.RevalidateRegistrationCredentialsErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.ValidateChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.ValidateChangePasswordErrorResponseErrorsDto
import com.mcs.emkn.network.dto.errorresponse.ValidateEmailErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.ValidateEmailErrorResponseErrorsDto
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
import com.mcs.emkn.network.dto.response.TokenDto
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
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            ResponseWithTokenAndTimeDto(TokenAndTimeDto( "5", "hdjshd")),
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            BeginChangePasswordErrorResponseDto(
                BeginChangePasswordErrorResponseErrorsDto(
                    NetError("1")
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun accountsValidateChangePassword(request: ValidateChangePasswordRequestDto): NetworkResponse<ResponseWithTokenDto, ValidateChangePasswordErrorResponseDto> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            ResponseWithTokenDto(TokenDto( "hdjshd")),
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            ValidateChangePasswordErrorResponseDto(
                ValidateChangePasswordErrorResponseErrorsDto(
                    NetError("1"), null
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun accountsCommitChangePassword(request: CommitChangePasswordRequestDto): NetworkResponse<Unit, CommitChangePasswordErrorResponseDto> {
        delay(1000)
        val success = true
        return if (success) NetworkResponse.Success(
            Unit,
            Response.success(200, "OK")
        )
        else NetworkResponse.ServerError(
            CommitChangePasswordErrorResponseDto(
                CommitChangePasswordErrorResponseErrorsDto(
                    NetError("1"), null
                )
            ), response = Response.success(200, "BAD")
        )
    }

    override suspend fun accountsRevalidateRegistrationCredentials(request: RevalidateCredentialsDto):
        NetworkResponse<ResponseWithTokenAndTimeDto, RevalidateRegistrationCredentialsErrorResponseDto> {
        delay(1000)
        TODO()
    }
}