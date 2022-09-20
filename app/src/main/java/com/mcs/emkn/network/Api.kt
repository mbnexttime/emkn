package com.mcs.emkn.network

import com.mcs.emkn.network.dto.RegistrationResponseDto

interface Api {
    fun accountsRegister(
        login: String,
        password: String,
        email: String,
        firstName: String,
        lastName: String,
    ): RegistrationResponseDto
}