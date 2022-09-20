package com.mcs.emkn.network.dto

import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistrationResponseDto(
    @Json(name = "random_token") val randomToken: String,
    @Json(name= "expires_at") val expiresAt: String,
)