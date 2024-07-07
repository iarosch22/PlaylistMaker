package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<Button>(R.id.btn_back)

        btnBack.setOnClickListener {
            val backIntent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(backIntent)
        }
    }
}