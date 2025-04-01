package com.practicum.playlistmaker.library.ui.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.LibraryInteractor
import com.practicum.playlistmaker.library.ui.FavoriteState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch

class FavoriteViewModel(private val libraryInteractor: LibraryInteractor): ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    init {
        getFavoriteTrack()
    }



    private fun getFavoriteTrack() {
        viewModelScope.launch {
            libraryInteractor
                .favoriteTracks()
                .collect{ tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(tracks: List<Track>?) {
        if (!tracks.isNullOrEmpty()) {
            setState(FavoriteState.Content(tracks))
        } else {
            setState(FavoriteState.Empty)
        }
    }

    private fun setState(state: FavoriteState) {
        stateLiveData.postValue(state)
    }

}