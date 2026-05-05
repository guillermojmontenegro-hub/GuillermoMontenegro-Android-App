package com.example.cvguillermomontenegro.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockArticleInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.trim('/')

        if (!path.startsWith("articles")) {
            return chain.proceed(request)
        }

        val payload = context.assets.open("mock/articles.json").bufferedReader().use { it.readText() }

        val responseJson = when {
            path == "articles" -> payload
            path.startsWith("articles/") -> {
                val slug = path.removePrefix("articles/")
                val root = json.parseToJsonElement(payload).jsonObject
                val articles = root["articles"]?.jsonArray.orEmpty()
                val article = articles.firstOrNull { element ->
                    element.jsonObject["slug"] == JsonPrimitive(slug)
                }?.jsonObject ?: buildJsonObject { put("error", JsonPrimitive("Article not found")) }
                json.encodeToString(JsonObject.serializer(), article)
            }
            else -> """{"error":"Unsupported route"}"""
        }

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(if (responseJson.contains("Article not found")) 404 else 200)
            .message("OK")
            .body(responseJson.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
