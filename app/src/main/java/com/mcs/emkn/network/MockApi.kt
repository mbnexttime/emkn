package com.mcs.emkn.network

import com.mcs.emkn.network.dto.request.*
import com.mcs.emkn.network.dto.response.ResponseEmptyDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
import retrofit2.Call

class MockApi: Api {
    override suspend fun accountsRegister(request: RegistrationRequestDto): AuthResponse<ResponseWithTokenDto> {
        TODO("Not yet implemented")
    }

    override suspend fun validateEmail(request: ValidateEmailRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsLogin(request: LoginRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsChangePassword(request: ChangePasswordRequestDto): AuthResponse<ResponseWithTokenDto> {
        TODO("Not yet implemented")
    }

    override suspend fun accountsValidateChangePassword(request: ValidateChangePasswordRequestDto): AuthResponse<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }
}