package com.mcs.emkn.network.dto.errorresponse

import com.mcs.emkn.network.dto.error.NetError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ValidateChangePasswordErrorResponseDto(
    @Json(name = "code_invalid") val codeInvalid: NetError?,
    @Json(name = "password_change_expired") val passwordChangeExpired: NetError?,
)
