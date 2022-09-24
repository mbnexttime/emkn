package com.mcs.emkn.network.dto.response

import com.mcs.emkn.network.dto.error.NetError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseWithTokenAndTimeDto(
    @Json(name = "random_token") val randomToken: String,
    @Json(name = "expires_at") val expiresAt: String,
    @Json(name = "errors") override val errors: List<NetError>
) : AuthResponseDto