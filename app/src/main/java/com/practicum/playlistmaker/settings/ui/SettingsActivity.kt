package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor


class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        themeSwitcher = binding.themeSwitcher

        settingsInteractor = Creator.provideSettingsInteractor(this)

        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            settingsInteractor.saveThemePreferences(checked)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnShare.setOnClickListener {
            val message = getString(R.string.app_course_link)
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        binding.btnSupport.setOnClickListener {
            val subject = getString(R.string.app_subject_message)
            val message = getString(R.string.app_message)
            val userMail = getString(R.string.app_user_mail)
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))

            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(userMail))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(intent)
        }

        binding.btnTerms.setOnClickListener {
            val link = getString(R.string.app_terms_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))

            startActivity(intent)
        }
    }

}