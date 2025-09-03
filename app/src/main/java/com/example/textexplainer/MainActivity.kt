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
        findViewById<Button>(R.id.btn_explain).setOnClickListener {
            val text = input.text?.toString() ?: ""
            val i = Intent(this, ExplainActivity::class.java)
                .putExtra("text", text)
            startActivity(i)
        }
    }
}
