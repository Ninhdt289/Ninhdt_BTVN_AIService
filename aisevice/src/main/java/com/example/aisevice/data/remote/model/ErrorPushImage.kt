package com.example.aisevice.data.remote.model

data class ErrorPushImage(
    override val cause: Throwable? = null,
    override val message: String? = null,
    val code: Int? = null
) : Throwable(cause = cause, message = message)