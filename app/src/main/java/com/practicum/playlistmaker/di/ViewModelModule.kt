package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.ui.view_model.TracksSearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModule = module {

    viewModel {
        TracksSearchViewModel(get())
    }

}