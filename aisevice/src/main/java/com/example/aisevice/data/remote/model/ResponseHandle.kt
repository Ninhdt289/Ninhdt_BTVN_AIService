package com.example.aisevice.data.remote.model


import com.apero.aigenerate.network.model.segment.ResponseSegment

import com.example.aisevice.data.client.ApiClient
import com.example.aisevice.data.remote.ServiceError.CODE_TIMEOUT_ERROR
import com.example.aisevice.data.remote.ServiceError.CODE_UNKNOWN_ERROR
import com.example.aisevice.data.remote.ServiceError.ERROR_TIME_OUT_MESSAGE
import com.example.aisevice.data.remote.ServiceError.UNKNOWN_ERROR_MESSAGE
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.seconds

internal suspend fun Call<ResponseBody>.enqueueCallResult(): ResponseState<Response<ResponseBody>, Throwable> =
    withTimeoutOrNull(ApiClient.REQUEST_TIMEOUT.seconds) {
        suspendCoroutine { continuation ->
            this@enqueueCallResult.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    when {
                        !response.isSuccessful -> {
                            continuation.resume(
                                ResponseState.Error(
                                    Throwable(response.errorBody().toString()),
                                    response.code()
                                )
                            )
                        }

                        response.body() != null -> {
                            continuation.resume(ResponseState.Success(response))
                        }


                        else -> {
                            continuation.resume(
                                ResponseState.Error(
                                    Throwable(UNKNOWN_ERROR_MESSAGE),
                                    response.code()
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    continuation.resume(ResponseState.Error(t, CODE_UNKNOWN_ERROR))
                }
            })
        }
    } ?: ResponseState.Error(Throwable(ERROR_TIME_OUT_MESSAGE), CODE_TIMEOUT_ERROR)


internal suspend fun Call<ResponseSegment>.enqueueCallSegmentResult(): ResponseState<Response<ResponseSegment>, Throwable> =
    withTimeoutOrNull(ApiClient.REQUEST_TIMEOUT.seconds) {
        suspendCoroutine { continuation ->
            this@enqueueCallSegmentResult.enqueue(object : Callback<ResponseSegment> {
                override fun onResponse(
                    call: Call<ResponseSegment>,
                    response: Response<ResponseSegment>
                ) {
                    when {
                        !response.isSuccessful -> {
                            continuation.resume(
                                ResponseState.Error(
                                    Throwable(response.errorBody().toString()),
                                    response.code()
                                )
                            )
                        }

                        response.body() != null -> {
                            continuation.resume(ResponseState.Success(response))
                        }

                        else -> {
                            continuation.resume(
                                ResponseState.Error(
                                    Throwable(UNKNOWN_ERROR_MESSAGE),
                                    response.code()
                                )
                            )

                        }
                    }
                }

                override fun onFailure(call: Call<ResponseSegment>, t: Throwable) {
                    continuation.resume(ResponseState.Error(t, CODE_UNKNOWN_ERROR))
                }
            })
        }
    } ?: ResponseState.Error(Throwable(ERROR_TIME_OUT_MESSAGE), CODE_TIMEOUT_ERROR)
