package com.mcs.emkn.network.dto.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationRequestDto(
    @Json(name = "login") val login: String,
    @Json(name = "password") val password: String,
    @Json(name = "email") val email: String,
    @Json(name = "firstname") val firstname: String,
    @Json(name = "lastname") val lastname: String
)