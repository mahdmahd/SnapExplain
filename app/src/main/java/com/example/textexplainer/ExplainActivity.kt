package com.example.textexplainer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExplainActivity : AppCompatActivity() {
    private lateinit var preview: TextView
    private lateinit var result: TextView
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explain)

        preview = findViewById(R.id.preview)
        result = findViewById(R.id.result)
        progress = findViewById(R.id.progress)
        
        val selected = getIncomingText()
        preview.text = if (selected.length <= 400) selected else selected.substring(0, 400) + "…"


        lifecycleScope.launch {
            val explanation = withContext(Dispatchers.IO) {
                try {
                    LlmClient.explain(
                        "Explain the following text in under 100 words, in clear, simple language.\n\n\"$selected\""
                    )
                } catch (e: Exception) {
                    "Error: ${e.message ?: "failed to get explanation."}"
                }
            }
            progress.visibility = View.GONE
            result.visibility = View.VISIBLE
            result.text = enforceWordLimit(explanation, 100)
        }

        findViewById<Button>(R.id.btn_copy).setOnClickListener {
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("explanation", result.text))
        }
        findViewById<Button>(R.id.btn_close).setOnClickListener { finish() }
        overridePendingTransition(0, 0)
    }

    private fun enforceWordLimit(text: String, maxWords: Int): String {
        val words = text.trim().split(Regex("\\s+"))
        return if (words.size <= maxWords) text else words.take(maxWords).joinToString(" ") + "…"
    }
        // ExplainActivity.kt  (add this method)
    private fun getIncomingText(): String {
        return when (intent.action) {
            Intent.ACTION_PROCESS_TEXT ->
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            Intent.ACTION_SEND ->
                intent.getStringExtra(Intent.EXTRA_TEXT)
            else ->
                intent.getStringExtra("text") // from our Tile or launcher paste
        } ?: ""
    }

}
