package com.practicum.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.ui.App


class SettingsActivity: AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeSwitcher = findViewById(R.id.themeSwitcher)

        settingsInteractor = Creator.provideSettingsInteractor(this)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            settingsInteractor.saveThemePreferences(checked)
        }

        val btnBack = findViewById<ViewGroup>(R.id.btn_back)
        btnBack.setOnClickListener { finish() }

        val btnShare = findViewById<ViewGroup>(R.id.btn_share)
        btnShare.setOnClickListener {
            val message = getString(R.string.app_course_link)
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        val btnSupport = findViewById<ViewGroup>(R.id.btn_support)
        btnSupport.setOnClickListener {
            val subject = getString(R.string.app_subject_message)
            val message = getString(R.string.app_message)
            val userMail = getString(R.string.app_user_mail)
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userMail))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        val btnTerms = findViewById<ViewGroup>(R.id.btn_terms)
        btnTerms.setOnClickListener {
            val link = getString(R.string.app_terms_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

            startActivity(intent)
        }
    }

}