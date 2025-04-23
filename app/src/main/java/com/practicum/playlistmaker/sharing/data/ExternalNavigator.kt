package com.practicum.playlistmaker.sharing.data

import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

interface ExternalNavigator {
    fun shareLink()
    fun openLink()
    fun openEmail()
}