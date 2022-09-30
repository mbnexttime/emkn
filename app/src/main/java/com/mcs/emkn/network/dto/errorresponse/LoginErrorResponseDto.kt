package com.mcs.emkn.network.dto.errorresponse

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginErrorResponseDto (
    @Json(name = "errors") val errors: LoginErrorResponseErrorsDto
)