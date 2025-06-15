package com.example.aisevice.data.remote.model

data class ErrorPresignedLink(
    override val cause: Throwable? = null,
    override val message: String? = null,
    val code: Int? = null
) : Throwable(cause = cause, message = message)