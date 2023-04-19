package net.turbovadim.bisquithost.network.Requests

import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

fun okHttpGetRequest(url: String, apiKey: String?): Request {
    return Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json")
        .addHeader("Authorization", "Bearer $apiKey")
        .build()
}

fun okHttpPostRequest(
    url: String,
    apiKey: String?,
    postBody: String,
    contentType: String = "application/json"
): Request {
    return Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .addHeader("Content-Type", contentType)
        .addHeader("Authorization", "Bearer $apiKey")
        .post(postBody.toRequestBody())
        .build()
}

//fun okHttpPostRequest2(url: String, apiKey: String?, postBody: String): Request {
//    return Request.Builder()
//        .url(url)
//        .header("Accept", "application/json")
//        .addHeader("Authorization", "Bearer $apiKey")
//        .post(postBody.toRequestBody())
//        .build()
//}