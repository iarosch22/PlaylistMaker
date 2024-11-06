package com.practicum.playlistmaker.player.presentation.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.presentation.PlayerUiState
import com.practicum.playlistmaker.search.presentation.view_model.TracksSearchViewModel

class PlayerViewModel(private val trackUrl: String): ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerUiState>()
    fun observeState(): LiveData<PlayerUiState> = stateLiveData

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TracksSearchViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

}