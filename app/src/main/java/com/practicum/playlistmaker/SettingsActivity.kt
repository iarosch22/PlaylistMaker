package com.practicum.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ViewGroup>(R.id.btn_back)
        btnBack.setOnClickListener {
            this.finish()
        }

        val btnShare = findViewById<ViewGroup>(R.id.btn_share)
        btnShare.setOnClickListener {
            val message = "https://practicum.yandex.ru/android-developer/?from=catalog"
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }
    }

}