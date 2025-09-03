package com.example.textexplainer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val input = findViewById<EditText>(R.id.input)
        val etLang = findViewById<EditText>(R.id.et_lang)
        val etMaxTokens = findViewById<EditText>(R.id.et_max_tokens)
        val etTemp = findViewById<EditText>(R.id.et_temperature)
        val etWordLimit = findViewById<EditText>(R.id.et_word_limit)
        val etPrompt = findViewById<EditText>(R.id.et_prompt)
        val etApiKey = findViewById<EditText>(R.id.et_api_key)

        // Load current settings
        etLang.setText(Settings.getLang(this))
        etMaxTokens.setText(Settings.getMaxTokens(this).toString())
        etTemp.setText(Settings.getTemperature(this).toString())
        etWordLimit.setText(Settings.getWordLimit(this).toString())
        etPrompt.setText(Settings.getPrompt(this))
        etApiKey.setText(Settings.getApiKey(this))

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            Settings.setLang(this, etLang.text?.toString()?.ifBlank { "fa" } ?: "fa")
            Settings.setMaxTokens(this, etMaxTokens.text?.toString()?.toIntOrNull() ?: 600)
            Settings.setTemperature(this, etTemp.text?.toString()?.toFloatOrNull() ?: 0.3f)
            Settings.setWordLimit(this, etWordLimit.text?.toString()?.toIntOrNull() ?: 1000)
            Settings.setPrompt(this, etPrompt.text?.toString()?.ifBlank { Settings.getPrompt(this) } ?: Settings.getPrompt(this))
            Settings.setApiKey(this, etApiKey.text?.toString()?.trim() ?: "")
        }

        findViewById<Button>(R.id.btn_explain).setOnClickListener {
            val text = input.text?.toString() ?: ""
            startActivity(Intent(this, ExplainActivity::class.java).putExtra("text", text))
        }
    }
}
