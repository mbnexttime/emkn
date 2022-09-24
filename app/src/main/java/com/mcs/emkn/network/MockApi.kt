package com.mcs.emkn.network

import com.mcs.emkn.network.dto.request.LoginRequestDto
import com.mcs.emkn.network.dto.request.RegistrationRequestDto
import com.mcs.emkn.network.dto.request.ValidateEmailRequestDto
import com.mcs.emkn.network.dto.response.ResponseEmptyDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
import retrofit2.Call

class MockApi: Api {
    override fun accountsRegister(request: RegistrationRequestDto): Call<ResponseWithTokenDto> {
        TODO()
    }

    override fun validateEmail(request: ValidateEmailRequestDto): Call<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }

    override fun accountsLogin(request: LoginRequestDto): Call<ResponseEmptyDto> {
        TODO("Not yet implemented")
    }
}