package com.mcs.emkn.network.dto.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProfilesGetResponseDto(
    @Json(name = "profiles") val profiles: List<ProfileDto>
)

@JsonClass(generateAdapter = true)
data class ProfileDto(
    @Json(name = "id") val id: Int,
    @Json(name = "avatar_url") val avatarUrl: String,
    @Json(name = "first_name") val firstName: String,
    @Json(name = "second_name") val secondName: String,
)
