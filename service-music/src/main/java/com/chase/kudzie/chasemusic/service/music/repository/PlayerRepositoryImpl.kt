package com.chase.kudzie.chasemusic.service.music.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.chase.kudzie.chasemusic.service.music.model.MediaItem
import javax.inject.Inject
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.chase.kudzie.chasemusic.service.music.data.MediaMetadataListener
import com.chase.kudzie.chasemusic.service.music.injection.scope.ServiceContext

/**
 * @author Kudzai Chasinda
 */

class PlayerRepositoryImpl @Inject constructor(
    @ServiceContext private val context: Context,
    private val metadataListener: MediaMetadataListener
) : PlayerRepository, DefaultLifecycleObserver {

    private val trackSelector = DefaultTrackSelector()
    private var player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
    private val userAgent: String = Util.getUserAgent(context, "ChaseMusic")
    private val dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
    private val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun resume() {
        player.playWhenReady = true
    }

    override fun play(mediaItem: MediaItem, hasTrackEnded: Boolean) {
        val media =
            ConcatenatingMediaSource(
                mediaSource.createMediaSource(getSongUri(mediaItem.id))
            )
        metadataListener.onMetadataChanged(mediaItem)
        player.prepare(media, true, true)
    }

    override fun pause(isServiceAlive: Boolean) {
        if (isServiceAlive) {
            player.playWhenReady = false
        }
    }

    override fun seekTo(milliseconds: Long) {
        player.seekTo(milliseconds)
    }

    override fun likeTrack() {
        TODO("This supposed to make a database or playlist entry into favorites.")
    }

    override fun prepare(mediaItem: MediaItem) {
        val media =
            ConcatenatingMediaSource(
                mediaSource.createMediaSource(getSongUri(mediaItem.id))
            )
        metadataListener.onMetadataChanged(mediaItem)
        player.prepare(media)
        player.playWhenReady = false
        player.seekTo(0L)

    }

    override fun getDuration(): Long = player.duration


    private fun getSongUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        player.release()
    }
}