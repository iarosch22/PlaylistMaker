package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.App


class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var themeSwitcher: SwitchMaterial

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        themeSwitcher = binding.themeSwitcher

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(
            (applicationContext as App),
            Creator.provideSharingInteractor(this),
            Creator.provideSettingsInteractor()
        ))[SettingsViewModel::class.java]

        themeSwitcher.isChecked = viewModel.getThemePreference()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.switchTheme(checked)
        }

        binding.btnBack.setOnClickListener { finish() }

        binding.btnShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.btnSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.btnTerms.setOnClickListener {
            viewModel.openTerms()
        }
    }

}