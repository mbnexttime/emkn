package com.mcs.emkn.network.dto.errorresponse

import com.mcs.emkn.network.dto.error.NetError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class BeginChangePasswordErrorResponseDto(
    @Json(name = "invalid_login_or_email") val invalidLoginOrEmail: NetError?,
    @Json(name = "invalid_new_password") val invalidNewPassword: NetError?,
)

