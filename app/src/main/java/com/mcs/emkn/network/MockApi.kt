package com.mcs.emkn.network

import com.mcs.emkn.network.dto.RegistrationResponseDto

class MockApi: Api {
    override fun accountsRegister(
        login: String,
        password: String,
        email: String,
        firstName: String,
        lastName: String
    ): RegistrationResponseDto {
        return RegistrationResponseDto("123", "123543953495843958439584395834")
    }
}