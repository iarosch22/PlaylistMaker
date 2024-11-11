package com.practicum.playlistmaker.sharing.domain.model

data class EmailData(
    val subject: String,
    val message: String,
    val userMail: String
) {}