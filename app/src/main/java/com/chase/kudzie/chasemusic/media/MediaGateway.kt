package com.chase.kudzie.chasemusic.media

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.extensions.canReadStorage
import com.chase.kudzie.chasemusic.extensions.distinctUntilChanged
import com.chase.kudzie.chasemusic.media.connection.ConnectionState
import com.chase.kudzie.chasemusic.media.connection.IMediaConnectionCallback
import com.chase.kudzie.chasemusic.media.connection.MusicServiceConnection
import com.chase.kudzie.chasemusic.media.connection.OnConnectionChangedListener
import com.chase.kudzie.chasemusic.media.controller.IMediaControllerCallback
import com.chase.kudzie.chasemusic.media.controller.MediaControllerCallback
import com.chase.kudzie.chasemusic.media.extensions.toPlayableItem
import com.chase.kudzie.chasemusic.media.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.IllegalStateException


class MediaGateway(
    private val context: Context,
    private val onConnectionChanged: OnConnectionChangedListener
) : CoroutineScope by MainScope(),
    IMediaControllerCallback,
    IMediaConnectionCallback {

    private val mediaBrowser: MediaBrowserCompat by lazy(LazyThreadSafetyMode.NONE) {
        MediaBrowserCompat(
            context,
            ComponentName(context, "com.chase.kudzie.chasemusic.service.music.MusicService"),
            MusicServiceConnection(this),
            null
        )
    }

    val callback: MediaControllerCompat.Callback = MediaControllerCallback(this)
    private var job: Job? = null

    private val connectionFlow = MutableSharedFlow<ConnectionState>()
    private val queueFlow = MutableSharedFlow<List<PlayableMediaItem>>()

    private val metadataLiveData = MutableLiveData<MediaMetadata>()
    private val playbackStateLiveData = MutableLiveData<MediaPlaybackState>()
    private val shuffleModeLiveData = MutableLiveData<MediaShuffleMode>()
    private val repeatModeLiveData = MutableLiveData<MediaRepeatMode>()

    fun connect() {
        if (!canReadStorage(context)) {
            return
        }
        job?.cancel()

        job = launch {
            connectionFlow.collect { state ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        onConnectionChanged.onConnectionSuccess(mediaBrowser, callback)
                    }
                    ConnectionState.FAILED -> {
                        onConnectionChanged.onConnectionFailed(mediaBrowser, callback)
                    }
                }
            }
        }

        if (!mediaBrowser.isConnected) {
            try {
                mediaBrowser.connect()
            } catch (ex: IllegalStateException) {
                Log.e("MediaGateway", ex.message!!)
            }
        }
    }

    fun disconnect() {
        job?.cancel()
        if (mediaBrowser.isConnected) {
            mediaBrowser.disconnect()
        }
    }

    fun initialize(mediaController: MediaControllerCompat) {
        callback.onMetadataChanged(mediaController.metadata)
        callback.onPlaybackStateChanged(mediaController.playbackState)
        callback.onQueueChanged(mediaController.queue)
        callback.onRepeatModeChanged(mediaController.repeatMode)
        callback.onShuffleModeChanged(mediaController.shuffleMode)
    }


    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        if (metadata != null) {
            metadataLiveData.value = MediaMetadata(metadata)
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.let {
            playbackStateLiveData.value = MediaPlaybackState(state)
        }
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        shuffleModeLiveData.value = MediaShuffleMode(shuffleMode)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        repeatModeLiveData.value = MediaRepeatMode(repeatMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        if (queue == null)
            return

        launch(Dispatchers.Default) {
            val result = queue.map { it.toPlayableItem() }
            queueFlow.emit(result)
        }
    }

    override fun onConnectionStateChanged(state: ConnectionState) {
        launch(Dispatchers.Main) {
            connectionFlow.emit(state)
        }
    }

    fun observeMetadata(): LiveData<MediaMetadata> = metadataLiveData
        .distinctUntilChanged()

    fun observePlaybackState(): LiveData<MediaPlaybackState> = playbackStateLiveData
        .distinctUntilChanged()

    fun observeShuffleMode(): LiveData<MediaShuffleMode> =
        shuffleModeLiveData.distinctUntilChanged()

    fun observeRepeatMode(): LiveData<MediaRepeatMode> =
        repeatModeLiveData.distinctUntilChanged()

    fun observePlayingQueue(): Flow<List<PlayableMediaItem>> = queueFlow.distinctUntilChanged()
}