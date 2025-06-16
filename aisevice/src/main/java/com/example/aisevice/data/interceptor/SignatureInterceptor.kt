package com.example.aisevice.data.interceptor
import com.apero.signature.SignatureParser
import com.example.aisevice.ultils.Key
import com.example.aisevice.ultils.ServiceConst
import okhttp3.Interceptor
import okhttp3.Response

internal class SignatureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val signatureData = SignatureParser.parseData(
            Key.API_KEY,
            Key.PUBLIC_KEY,
            TimeStampManage.timeStamp
        )
        val tokenIntegrity = signatureData.tokenIntegrity.ifEmpty { ServiceConst.NOT_GET_API_TOKEN}

        val headers = hashMapOf(
            "Accept" to "application/json",
            "Content-Type" to "application/json",
            "device" to "android",
            "x-api-signature" to signatureData.signature,
            "x-api-timestamp" to signatureData.timeStamp.toString(),
            "x-api-keyid" to Key.API_KEY,
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
