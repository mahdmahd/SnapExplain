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
    private const val API_KEY  = "aa-ud4ZNNDkJpLBw4Om9z7vnwsejt7bsWB7VETuKx2OBX71d8oq"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun explain(text: String, lang: String = "en"): String {
        if (API_KEY.isBlank()) return "API key missing."

        val prompt = """Explain the following text in under 1000 words, clear and simple (in persian) ($lang).

"$text"
""".trimIndent()

        val payload = JSONObject()
            .put("model", "gpt-5-mini")
            .put("messages", JSONArray().put(
                JSONObject().put("role","user").put("content", prompt)
            ))
            .put("temperature", 0.2)
            .put("max_tokens", 220)

        val req = Request.Builder()
            .url("$BASE_URL/chat/completions")
            .header("Authorization", "Bearer $API_KEY")
            .post(payload.toString().toRequestBody("application/json".toMediaType()))
            .build()

        client.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) error("HTTP ${resp.code}")
            val body = resp.body?.string().orEmpty()
            val json = JSONObject(body)
            return json.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim()
        }
    }
}
