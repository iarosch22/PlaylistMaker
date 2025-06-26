package com.practicum.playlistmaker.player.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class MusicService: Service(), AudioPlayerControl {

    private val binder = MusicServiceBinder()

    private val _playerState = MutableStateFlow<PlayerUiState>(PlayerUiState.Default)
    private val playerStateFlow = _playerState.asStateFlow()

    private var songUrl = ""
    private var artistName = ""
    private var songTitle = ""


    private var mediaPlayer: MediaPlayer? = null

    private var timerJob: Job? = null

    private fun startTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(CHECK_INTERVAL)
                _playerState.value = PlayerUiState.Playing(getCurrentPlayerPosition())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        releasePlayer()
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(SONG_URL) ?: ""
        artistName = intent?.getStringExtra(ARTIST_NAME) ?: ""
        songTitle = intent?.getStringExtra(SONG_TITLE) ?: ""
        initMediaPlayer()

        createNotificationChannel()

        return binder
    }

    override fun startNotification() {
        ServiceCompat.startForeground(
            this,
            SERVICE_NOTIFICATION_ID,
            createServiceNotification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun stopNotification() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        releasePlayer()
        return super.onUnbind(intent)
    }

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private fun initMediaPlayer() {
        if (songUrl.isEmpty()) return

        mediaPlayer?.setDataSource(songUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerUiState.Prepared
        }
        mediaPlayer?.setOnCompletionListener {
            timerJob?.cancel()
            stopNotification()
            _playerState.value = PlayerUiState.Completed
        }
    }

    override fun getPlayerState(): StateFlow<PlayerUiState> {
        return playerStateFlow
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerState.value = PlayerUiState.Playing(getCurrentPlayerPosition())
        startTimer()
    }

    override fun pausePlayer() {
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerState.value = PlayerUiState.Paused(getCurrentPlayerPosition())
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition) ?: "00:00"
    }

    private fun releasePlayer() {
        if (mediaPlayer == null) return

        timerJob?.cancel()

        mediaPlayer?.apply {
            stop()
            setOnPreparedListener(null)
            setOnCompletionListener(null)
            release()
        }
        mediaPlayer = null

        _playerState.value = PlayerUiState.Default
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        channel.description = getString(R.string.app_service_name)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createServiceNotification() : Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(artistName)
            .setContentText(songTitle)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    companion object {

        private const val CHECK_INTERVAL = 300L

        private const val SONG_URL = "song_url"
        private const val ARTIST_NAME = "artist_name"
        private const val SONG_TITLE = "song_title"

        private const val NOTIFICATION_CHANNEL_ID = "music_service_channel"

        const val SERVICE_NOTIFICATION_ID = 100

    }

}