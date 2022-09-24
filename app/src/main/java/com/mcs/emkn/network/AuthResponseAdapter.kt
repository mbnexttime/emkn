package com.mcs.emkn.network

import com.mcs.emkn.network.dto.response.AuthResponseDto
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type


class AuthResponseAdapter<T: AuthResponseDto>(
    private val successType: Type,
) : CallAdapter<T, Call<AuthResponse<T>>> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<T>): Call<AuthResponse<T>> {
        return AuthCall(call)
    }
}