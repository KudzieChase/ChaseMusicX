package com.chase.kudzie.chasemusic.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    fun setShuffleMode(shuffleMode: Int)

    fun setRepeatMode(repeatMode: Int)

    suspend fun setCurrentQueuePosition(positionInQueue: Int)

    fun setCurrentPlayMode(playMode: Int)

    fun setCurrentSongDurationPos(currentDuration: Long)

    fun getShuffleMode(): Int

    fun getRepeatMode(): Int

    fun observeCurrentQueuePosition(): Flow<Int>

    fun getCurrentQueuePosition(): Int

    fun getCurrentPlayMode(): Int

    fun getCurrentSongDurationPos(): Long

    suspend fun setAlbumSongSortOrder(sortOrder: String)

    fun getAlbumSongSortOrder(): String

    fun observeAlbumSongSortOrder(): Flow<String>

    suspend fun setArtistSongOrder(sortOrder: String)

    fun getArtistSongSortOrder(): String

    fun observeArtistSongOrder(): Flow<String>

    suspend fun setSongSortOrder(sortOrder: String)

    fun getSongSortOrder(): String

    fun observeSongSortOrder(): Flow<String>

    suspend fun setAlbumSortOrder(sortOrder: String)

    fun getAlbumSortOrder(): String

    fun observeAlbumSortOrder(): Flow<String>

    suspend fun setArtistSortOrder(sortOrder: String)

    fun getArtistSortOrder(): String

    fun observeArtistSortOrder(): Flow<String>

    suspend fun setPlaylistSortOrder(sortOrder: String)

    fun getPlaylistSortOrder(): String

    fun observePlaylistSortOrder(): Flow<String>
}