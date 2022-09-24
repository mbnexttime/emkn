package com.mcs.emkn.network

import com.mcs.emkn.network.dto.request.LoginRequestDto
import com.mcs.emkn.network.dto.request.RegistrationRequestDto
import com.mcs.emkn.network.dto.request.ValidateEmailRequestDto
import com.mcs.emkn.network.dto.response.ResponseEmptyDto
import com.mcs.emkn.network.dto.response.ResponseWithTokenDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("accounts/register")
    fun accountsRegister(
        @Body request: RegistrationRequestDto
    ): Call<ResponseWithTokenDto>

    @POST("accounts/validate_email")
    fun validateEmail(
        @Body request: ValidateEmailRequestDto
    ): Call<ResponseEmptyDto>

    @POST("accounts/login")
    fun accountsLogin(
        @Body request: LoginRequestDto
    ): Call<ResponseEmptyDto>
}