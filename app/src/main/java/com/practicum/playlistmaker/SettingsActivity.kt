package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
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

            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        val btnSupport = findViewById<ViewGroup>(R.id.btn_support)
        btnSupport.setOnClickListener {
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("vit.iarosch@yandex.by"))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        val btnTerms = findViewById<ViewGroup>(R.id.btn_terms)
        btnTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))

            startActivity(intent)
        }
    }

}