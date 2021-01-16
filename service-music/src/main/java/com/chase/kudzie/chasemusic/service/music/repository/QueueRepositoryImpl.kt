package com.chase.kudzie.chasemusic.service.music.repository

import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.chase.kudzie.chasemusic.domain.interactor.songs.GetSongsByCategory
import com.chase.kudzie.chasemusic.domain.model.MediaIdCategory
import com.chase.kudzie.chasemusic.domain.model.MediaItem
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import com.chase.kudzie.chasemusic.domain.repository.SongQueueRepository
import com.chase.kudzie.chasemusic.domain.repository.SongRepository
import com.chase.kudzie.chasemusic.service.music.data.RepeatMode
import com.chase.kudzie.chasemusic.service.music.extensions.getAlbumArtUri
import com.chase.kudzie.chasemusic.service.music.extensions.toMediaItem
import com.chase.kudzie.chasemusic.service.music.extensions.toPlayableMediaItem
import com.chase.kudzie.chasemusic.service.music.model.PlayableMediaItem
import com.chase.kudzie.chasemusic.shared.injection.coroutinescope.DefaultScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

internal class QueueRepositoryImpl @Inject constructor(
    private val songsQueueRepository: SongQueueRepository,
    private val getSongsByCategory: GetSongsByCategory,
    private val songRepository: SongRepository,
    private val preferencesRepository: PreferencesRepository,
    private val repeatMode: RepeatMode,
    private val mediaSession: MediaSessionCompat
) : QueueRepository,
    CoroutineScope by DefaultScope(),
    DefaultLifecycleObserver {

    private val songQueue = ArrayList<MediaItem>()
    private var currentQueuePosition: Int = -1

    private var queueStateJob: Job? = null

    private val queueFlow = MutableSharedFlow<List<MediaItem>>(replay = 0)
    private val queueFlowDebounced = MutableSharedFlow<List<MediaItem>>(replay = 0)

    init {
        launch {
            queueFlowDebounced.debounce(1100).collect {
                consumeQueue(it)
            }
        }

        launch {
            queueFlow.collect {
                consumeQueue(it)
            }
        }
    }

    private fun publishQueue(list: List<MediaItem>, emitNow: Boolean) {
        launch {
            if (emitNow) {
                queueFlow.emit(list)
            } else {
                queueFlowDebounced.emit(list)
            }
        }
    }

    private fun consumeQueue(list: List<MediaItem>) {
        val queue = list.map { it.toQueueItem() }
        mediaSession.setQueue(
            queue
        )
    }

    override fun isQueueEmpty(): Boolean {
        return songQueue.isEmpty()
    }

    override suspend fun skipToNext(isFromUser: Boolean): PlayableMediaItem? {
        if (isQueueEmpty()) {
            return null
        }
        val newPosition = updatePositionInQueue(isNext = true, isFromUser)

        if (newPosition in 0..songQueue.lastIndex) {
            currentQueuePosition = newPosition
            val resultTrack = songQueue.getOrNull(newPosition)
            //You know what we do! ✌
            publishQueue(songQueue, emitNow = true)
            saveQueuePosition(songQueue)
            return resultTrack?.toPlayableMediaItem()
        }
        return null
    }

    override suspend fun skipToPrevious(): PlayableMediaItem? {
        if (isQueueEmpty())
            return null

        val newPosition = updatePositionInQueue(isNext = false)

        if (newPosition in 0..songQueue.lastIndex) {
            currentQueuePosition = newPosition
            val resultTrack = songQueue.getOrNull(newPosition)
            //You know what we do! ✌
            publishQueue(songQueue, emitNow = true)
            saveQueuePosition(songQueue)
            return resultTrack?.toPlayableMediaItem()
        }

        return null
    }

    override suspend fun prepare(): PlayableMediaItem? {
        val songsQueue = retrieveQueueState()
        val lastPositionInQueue = preferencesRepository.getCurrentQueuePosition()

        val currentTrackIndex =
            songsQueue.indexOfFirst { song -> song.positionInQueue == lastPositionInQueue }

        val resultTrack = songsQueue.getOrNull(currentTrackIndex) ?: return null

        publishQueue(songsQueue, emitNow = true)
        updateSongQueue(songsQueue)

        currentQueuePosition = currentTrackIndex

        return resultTrack.toPlayableMediaItem()
    }

    private fun updateSongQueue(songs: List<MediaItem>) {
        songQueue.clear()
        songQueue.addAll(songs)
    }

    override suspend fun onPlayFromMediaId(mediaIdCategory: MediaIdCategory): PlayableMediaItem? {
        //The assumption is that we get all our songs without handling album stuff for now
        //Get all the songs and index them,
        //get the one song with this particular ID
        //update the new songs list with a position in queue
        val songId = mediaIdCategory.songId

        val allSongs = getSongsByCategory(mediaIdCategory)

        val queueSongs = allSongs.mapIndexed { index: Int, song: Song ->
            song.toIndexedSong(index).toMediaItem(mediaIdCategory)
        }

        updateSongQueue(queueSongs)

        val currentTrackIndex = queueSongs.indexOfFirst { song -> song.id == songId }
        currentQueuePosition = currentTrackIndex

        publishQueue(queueSongs, emitNow = true)
        saveQueueState(queueSongs)

        val resultTrack = queueSongs.getOrNull(currentTrackIndex) ?: return null
        //return the song to play
        return resultTrack.toPlayableMediaItem()

    }

    override suspend fun getCurrentPlayingSong(): PlayableMediaItem? {
        //Retrieve current playing song
        if (isQueueEmpty())
            return null
        return songQueue.getOrNull(currentQueuePosition)?.toPlayableMediaItem()
    }

    override suspend fun removeSongFromQueue(positionAt: Int) {
        if (isQueueEmpty()) {
            return
        }

        songQueue.removeAt(positionAt)

        publishQueue(songQueue, emitNow = true)
        saveQueueState(songQueue)
    }

    override suspend fun addSongToQueue(mediaIdCategory: MediaIdCategory) {
        val song = songRepository.getSong(mediaIdCategory.songId)

        //Add the song to the bottom of the queue.
        val bottomIndex: Int = if (isQueueEmpty()) {
            0
        } else {
            songQueue.last().positionInQueue + 1
        }

        val mediaItem = song.toIndexedSong(bottomIndex).toMediaItem(mediaIdCategory)
        songQueue.add(mediaItem)

        publishQueue(songQueue, emitNow = true)
        saveQueueState(songQueue)
    }

    override fun clearQueue() {
        songQueue.clear()
        publishQueue(songQueue, emitNow = true)
        saveQueueState(songQueue)
    }

    private fun updatePositionInQueue(isNext: Boolean, isFromUser: Boolean = false): Int {
        //TODO refactor and make it better
        //TODO maybe decouple next and previous functionality for cleaner code.
        return when {
            repeatMode.isRepeatModeOne() -> {
                repeatOne(isNext, isFromUser)
            }
            repeatMode.isRepeatModeAll() -> {
                repeatAll(isNext, isFromUser)
            }
            else -> {
                repeatNone(isNext, isFromUser)
            }
        }
    }

    private fun repeatOne(isNext: Boolean, isFromUser: Boolean): Int {
        var newQueuePosition = currentQueuePosition
        if (isNext && isFromUser) {
            if (newQueuePosition != songQueue.size - 1)
                newQueuePosition += 1
            else
                newQueuePosition = 0
        } else if (!isNext) {
            if (newQueuePosition != 0)
                newQueuePosition -= 1
            else
                newQueuePosition = songQueue.size - 1
        }
        return newQueuePosition
    }

    private fun repeatAll(isNext: Boolean, isFromUser: Boolean): Int {
        var newQueuePosition = currentQueuePosition
        if (isNext) {
            if (newQueuePosition != songQueue.size - 1)
                newQueuePosition += 1
            else
                newQueuePosition = 0
        } else {
            //This is previous
            if (newQueuePosition != 0)
                newQueuePosition -= 1
            else
                newQueuePosition = songQueue.size - 1
        }
        return newQueuePosition
    }

    private fun repeatNone(isNext: Boolean, isFromUser: Boolean): Int {
        var newQueuePosition = currentQueuePosition
        if (isNext && isFromUser) {
            if (newQueuePosition != songQueue.size - 1)
                newQueuePosition += 1
            else
                newQueuePosition = songQueue.size - 1
        } else if (isNext && !isFromUser) {
            newQueuePosition += 1
        } else {
            //This is previous
            if (newQueuePosition != 0)
                newQueuePosition -= 1
            else
                newQueuePosition = 0
        }

        return newQueuePosition
    }


    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        saveQueueState(songQueue)
    }

    override fun sortSongs() {
        if (isQueueEmpty()) {
            return
        }

        val currentTrack = songQueue.getOrNull(currentQueuePosition) ?: return
        songQueue.sortBy { it.positionInQueue }

        val newTrackIndex =
            songQueue.indexOfFirst { song -> song.positionInQueue == currentTrack.positionInQueue }
        currentQueuePosition = newTrackIndex

        publishQueue(songQueue, emitNow = true)
    }

    override fun shuffleSongs() {
        if (isQueueEmpty()) {
            return
        }

        val currentTrack = songQueue.getOrNull(currentQueuePosition) ?: return

        val queue = songQueue.shuffled()
        updateSongQueue(queue)

        val currentTrackIndex =
            queue.indexOfFirst { song -> song.positionInQueue == currentTrack.positionInQueue }

        if (currentTrackIndex != 0) {
            Collections.swap(queue, 0, currentTrackIndex)
        }
        currentQueuePosition = 0

        publishQueue(songQueue, emitNow = true)
    }

    override fun swap(from: Int, to: Int) {
        if (isQueueEmpty()) {
            return
        }

        if (from !in 0..songQueue.lastIndex || to !in 0..songQueue.lastIndex) {
            return
        }

        val currentSong = songQueue.getOrNull(currentQueuePosition) ?: return

        Collections.swap(songQueue, from, to)

        //Update current queue position
        val newQueuePosition =
            songQueue.indexOfFirst { song -> song.positionInQueue == currentSong.positionInQueue }
        currentQueuePosition = newQueuePosition

        publishQueue(songQueue, emitNow = false)
        saveQueueState(songQueue)
    }

    override suspend fun skipToQueueItem(id: Long): PlayableMediaItem? {

        if (isQueueEmpty()) {
            return null
        }

        val position = songQueue.indexOfFirst { song -> song.positionInQueue == id.toInt() }

        if (position == -1) {
            return null
        }


        currentQueuePosition = position
        publishQueue(songQueue, emitNow = true)
        saveQueueState(songQueue)

        return songQueue.getOrNull(currentQueuePosition)?.toPlayableMediaItem()
    }

    private fun saveQueueState(songs: List<MediaItem>) {
        queueStateJob?.cancel()
        queueStateJob = launch {
            songsQueueRepository.updateQueue(songs)
            saveQueuePosition(songs)
            yield()
        }
    }

    private suspend fun saveQueuePosition(songs: List<MediaItem>) {
        songs.getOrNull(currentQueuePosition)?.let {
            preferencesRepository.setCurrentQueuePosition(it.positionInQueue)
        }
    }

    private suspend fun retrieveQueueState(): List<MediaItem> {
        return songsQueueRepository.getQueueSongs()
    }

    private fun Song.toIndexedSong(index: Int): Song {
        return this.copy(positionInQueue = index)
    }

    private fun MediaItem.toQueueItem(): MediaSessionCompat.QueueItem {
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(mediaId.toString())
            .setTitle(this.title)
            .setSubtitle(this.artist)
            .setDescription(this.album)
            .setIconUri(getAlbumArtUri(this.albumId))
            .build()

        return MediaSessionCompat.QueueItem(description, positionInQueue.toLong())
    }
}