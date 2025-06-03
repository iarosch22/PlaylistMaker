package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import java.text.SimpleDateFormat
import java.util.Locale

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink() {
        val link = context.getString(R.string.app_course_link)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(shareIntent)
    }

    override fun openLink() {
        val link = context.getString(R.string.app_terms_link)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }

    override fun openEmail() {
        val subject = context.getString(R.string.app_subject_message)
        val message = context.getString(R.string.app_message)
        val userMail = context.getString(R.string.app_user_mail)

        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userMail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        context.startActivity(intent)
    }

}