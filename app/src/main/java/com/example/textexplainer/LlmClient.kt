package com.example.textexplainer

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object LlmClient {
    private const val BASE_URL = "https://api.avalai.ir/v1"
    // ⚠️ If you ship a real key here, rotate it if your APK is shared.
    private const val API_KEY  = "aa-ud4ZNNDkJpLBw4Om9z7vnwsejt7bsWB7VETuKx2OBX71d8oq"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun explain(
        text: String,
        lang: String = "fa",
        maxTokens: Int = 600,
        temperature: Float = 0.3f
    ): String {
        if (API_KEY.isBlank()) return "API key missing."

        val prompt = """
            توضیح بده متن زیر را با زبانی ساده و روان. 
            حداکثر ${maxTokens} توکن خروجی؛ اگر لازم است نکات کلیدی را فهرست کن. 
            زبان: ${if (lang == "fa") "فارسی" else lang}.
            
            «$text»
        """.trimIndent()

        val payload = JSONObject()
            // Make sure this model name exists on your AvalAI endpoint
            .put("model", "gpt-4o")
            .put("messages", JSONArray().put(
                JSONObject().put("role","user").put("content", prompt)
            ))
            .put("temperature", temperature.toDouble())
            .put("max_tokens", maxTokens)

        val req = Request.Builder()
            .url("$BASE_URL/chat/completions")
            .header("Authorization", "Bearer $API_KEY")
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
