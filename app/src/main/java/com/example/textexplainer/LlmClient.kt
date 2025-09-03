package com.example.textexplainer

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LlmClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun explain(
        text: String,
        lang: String = "fa",
        maxTokens: Int = 600,
        temperature: Float = 0.3f,
        promptTemplate: String = "Explain the following text clearly and simply.",
        apiKey: String,
        baseUrl: String,
        model: String
    ): String {
        if (apiKey.isBlank()) return "API key missing."
        val url = (baseUrl.ifBlank { "https://api.avalai.ir/v1" }).trimEnd('/')

        val prompt = """
            $promptTemplate
            Language: ${if (lang == "fa") "فارسی" else lang}.
            
            «$text»
        """.trimIndent()

        val payload = JSONObject()
            .put("model", model.ifBlank { "gpt-4o" })
            .put("messages", JSONArray().put(
                JSONObject().put("role","user").put("content", prompt)
            ))
            .put("temperature", temperature.toDouble())
            .put("max_tokens", maxTokens)

        val req = Request.Builder()
            .url("$url/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()

            if (!resp.isSuccessful) {
                val message = try {
                    JSONObject(body).optJSONObject("error")?.optString("message")
                } catch (_: Exception) { null }
                return "LLM error ${resp.code}: ${message ?: body.ifBlank { "unknown error" }}"
            }

            return try {
                val json = JSONObject(body)
                json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()
            } catch (e: Exception) {
                "Parse error: ${e.message}\n\n$body"
            }
        }
    }
}
