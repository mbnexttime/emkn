package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.errorresponse.BeginChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.CommitChangePasswordErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.LoginErrorResponseDto
import com.mcs.emkn.network.dto.errorresponse.RegistrationErrorResponseDto
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

class MockApi: Api {
    override suspend fun accountsRegister(request: RegistrationRequestDto): NetworkResponse<ResponseWithTokenAndTimeDto, RegistrationErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun validateEmail(request: ValidateEmailRequestDto): NetworkResponse<Unit, ValidateEmailErrorResponseDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsLogin(request: LoginRequestDto): NetworkResponse<Unit, LoginErrorResponseDto> {
        TODO("Not yet implemented")
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
        TODO("Not yet implemented")
    }
}