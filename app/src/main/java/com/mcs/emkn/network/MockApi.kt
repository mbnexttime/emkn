package com.mcs.emkn.network

import com.haroldadmin.cnradapter.NetworkResponse
import com.mcs.emkn.network.dto.errorresponse.*
import com.mcs.emkn.network.dto.request.*
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

}