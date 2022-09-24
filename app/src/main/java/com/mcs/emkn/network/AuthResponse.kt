package com.mcs.emkn.network

import com.mcs.emkn.network.dto.response.AuthResponseDto
import java.io.IOException

sealed class AuthResponse<out T: AuthResponseDto> {
    /**
     * Success response with body
     */
    data class Success<out T: AuthResponseDto>(val body: T) : AuthResponse<T>()

    /**
     * Failure response with body
     */
    data class ApiError<out T: AuthResponseDto>(val body: T) : AuthResponse<T>()

    /**
     * Network error
     */
    data class NetworkError(val error: IOException) : AuthResponse<Nothing>()

    /**
     * For example, json parsing error
     */
    data class UnknownError(val error: Throwable) : AuthResponse<Nothing>()
}