package com.example.aisevice.data.interceptor


import com.apero.signature.SignatureParser
import okhttp3.Interceptor
import okhttp3.Response

internal class SignatureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val signatureData = SignatureParser.parseData(
            "sk-ePKj7HupzKwrm0BBDpKgbcptFg6zhJL7Fx0ZpfOMzhTa0w2efS",
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5z8DrSdxAFy5ju27JzxUDGD5OdPRnKVrXPypiBVT7NK4ltgbcud3+Li3H1DiAFNvaSDPumZMEbAkfGWZ6s3KtiI7TRmZwQ2yyH6mug6GhrCLD6CZJUQ2CPmhO3JYTYOgN53E6hwm/Teb9I156S04qHjLLLBxk9Mklu5X06kdhMBYwHFAZ3oByeoWUryrQC0Mv9C5ZahKzoQNuJNL2sv+ws2e5Zaj8Rid4AjhvqB6dYhWP4QM+0IiNjs/j08aRgcyOrenbQEIieU+XF6mQWF2Jfg317e0KjWnpru+uPVVgrEn9rNvQeXu2u4SZhT6rnLQzBLbJrngNcNw3gXfxxsoowIDAQAB",
            TimeStampManage.timeStamp
        )
        val tokenIntegrity = signatureData.tokenIntegrity.ifEmpty { "NOT_GET_API_TOKEN" }

        val headers = hashMapOf(
            "Accept" to "application/json",
            "Content-Type" to "application/json",
            "device" to "android",
            "x-api-signature" to signatureData.signature,
            "x-api-timestamp" to signatureData.timeStamp.toString(),
            "x-api-keyid" to "sk-ePKj7HupzKwrm0BBDpKgbcptFg6zhJL7Fx0ZpfOMzhTa0w2efS",
            "x-api-token" to tokenIntegrity,
            "x-api-bundleId" to "for.techtrek",
            "App-name" to "Tecktrek",
        )
        val requestBuilder = chain.request().newBuilder()
        for ((key, value) in headers) {
            requestBuilder.addHeader(key, value)
        }
        return chain.proceed(requestBuilder.build())
    }
}
