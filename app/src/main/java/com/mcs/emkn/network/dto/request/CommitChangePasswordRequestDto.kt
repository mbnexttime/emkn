package com.mcs.emkn.network.dto.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommitChangePasswordRequestDto(
    @Json(name = "change_password_token") val change_password_token: String,
    @Json(name = "new_password") val new_password: String,
)