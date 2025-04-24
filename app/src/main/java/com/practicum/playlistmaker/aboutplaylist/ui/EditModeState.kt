package com.practicum.playlistmaker.aboutplaylist.ui

sealed interface EditModeState {

    object None: EditModeState

    data class Show(val playlistId: Long): EditModeState

}