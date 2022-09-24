package com.mcs.emkn.network

import com.mcs.emkn.network.dto.request.*
import com.mcs.emkn.network.dto.response.ResponseEmptyDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenAndTimeDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto

class MockApi: Api {
    override suspend fun accountsRegister(request: RegistrationRequestDto): AuthResponse<ResponseWithTokenAndTimeDto> {
        TODO("Not yet implemented")
    }

    override suspend fun validateEmail(request: ValidateEmailRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsLogin(request: LoginRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsChangePassword(request: ChangePasswordRequestDto): AuthResponse<ResponseWithTokenAndTimeDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsValidateChangePassword(request: ValidateChangePasswordRequestDto): AuthResponse<ResponseWithTokenDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsCommitChangePassword(request: CommitChangePasswordRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }
}