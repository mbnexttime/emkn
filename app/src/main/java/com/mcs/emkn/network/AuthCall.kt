package com.mcs.emkn.network

import com.mcs.emkn.network.dto.response.AuthResponseDto
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthCall<T: AuthResponseDto>(
    private val delegate: Call<T>,
) : Call<AuthResponse<T>> {
    override fun enqueue(callback: Callback<AuthResponse<T>>) {
        return delegate.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                val body = response.body()

                if (response.isSuccessful) {
                    if (body != null) {
                        if (body.errors.isEmpty())
                            callback.onResponse(
                                this@AuthCall,
                                Response.success(AuthResponse.Success(body))
                            )
                        else
                            callback.onResponse(
                                this@AuthCall,
                                Response.success(AuthResponse.ApiError(body))
                            )
                    } else {
                        // Response is successful but the body is null
                        callback.onResponse(
                            this@AuthCall,
                            Response.success(AuthResponse.UnknownError(Error("Body is null.")))
                        )
                    }
                } else {
                    callback.onResponse(
                        this@AuthCall,
                        Response.success(AuthResponse.UnknownError(Error("Code is not 200.")))
                    )
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                val authResponse = when (throwable) {
                    is IOException -> AuthResponse.NetworkError(throwable)
                    else -> AuthResponse.UnknownError(throwable)
                }
                callback.onResponse(this@AuthCall, Response.success(authResponse))
            }
        })
    }

    override fun isExecuted() = delegate.isExecuted

    override fun clone() = AuthCall(delegate.clone())

    override fun isCanceled() = delegate.isCanceled

    override fun cancel() = delegate.cancel()

    override fun execute(): Response<AuthResponse<T>> {
        throw UnsupportedOperationException("AuthResponseCall doesn't support execute")
    }

    override fun request(): Request = delegate.request()
    override fun timeout(): Timeout {
        TODO("Not yet implemented")
    }
}