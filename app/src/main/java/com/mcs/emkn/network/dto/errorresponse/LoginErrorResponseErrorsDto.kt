package com.mcs.emkn.network.dto.errorresponse

import com.mcs.emkn.network.dto.error.NetError
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class LoginErrorResponseErrorsDto(
    @Json(name = "illegal_login_or_email") val illegalLoginOrEmail: NetError?,
)