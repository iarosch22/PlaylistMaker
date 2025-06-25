package com.practicum.playlistmaker.player.ui

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
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
        mediaPlayer = MediaPlayer()
    }

    override fun onBind(intent: Intent?): IBinder {
        songUrl = intent?.getStringExtra(SONG_URL) ?: ""
        initMediaPlayer()
        return binder
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
        timerJob?.cancel()

        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null

        _playerState.value = PlayerUiState.Default
    }

    companion object {

        private const val CHECK_INTERVAL = 300L

        private const val SONG_URL = "song_url"

    }

}