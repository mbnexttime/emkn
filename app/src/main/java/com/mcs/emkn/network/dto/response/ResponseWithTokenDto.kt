package com.mcs.emkn.network.dto.response

import com.mcs.emkn.network.dto.error.NetError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWithTokenDto(
    @Json(name = "change_password_token") val randomToken: String,
)