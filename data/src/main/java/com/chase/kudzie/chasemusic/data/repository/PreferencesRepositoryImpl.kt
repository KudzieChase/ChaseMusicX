package com.chase.kudzie.chasemusic.data.repository

import android.content.SharedPreferences
import com.chase.kudzie.chasemusic.data.preferences.DataStoreManager
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.albumSongSortOrder
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.albumSortOrder
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.artistSongSortOrder
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.artistSortOrder
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.currentPositionInQueue
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.currentSongDurationPos
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.playMode
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.playlistSortOrder
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.repeatMode
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.shuffleMode
import com.chase.kudzie.chasemusic.data.preferences.SharedPreferencesFactory.songSortOrder
import com.chase.kudzie.chasemusic.data.sorting.SortOrder
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences,
    private val dataStore: DataStoreManager
) : PreferencesRepository {

    override fun setShuffleMode(shuffleMode: Int) {
        preferences.shuffleMode = shuffleMode
    }

    override fun setRepeatMode(repeatMode: Int) {
        preferences.repeatMode = repeatMode
    }

    override suspend fun setCurrentQueuePosition(positionInQueue: Int) {
        preferences.currentPositionInQueue = positionInQueue
        dataStore.setCurrentQueuePosition(positionInQueue)
    }

    override fun setCurrentPlayMode(playMode: Int) {
        preferences.playMode = playMode
    }

    override fun setCurrentSongDurationPos(currentDuration: Long) {
        preferences.currentSongDurationPos = currentDuration
    }

    override fun getShuffleMode(): Int {
        return preferences.shuffleMode
    }

    override fun getRepeatMode(): Int {
        return preferences.repeatMode
    }

    override fun observeCurrentQueuePosition(): Flow<Int> {
        return dataStore.getCurrentQueuePosition()
    }

    override fun getCurrentQueuePosition(): Int {
        return preferences.currentPositionInQueue
    }

    override fun getCurrentPlayMode(): Int {
        return preferences.playMode
    }

    override fun getCurrentSongDurationPos(): Long {
        return preferences.currentSongDurationPos
    }

    override suspend fun setAlbumSongSortOrder(sortOrder: String) {
        preferences.albumSongSortOrder = sortOrder
        dataStore.setAlbumSongSortOrder(sortOrder)
    }

    override fun getAlbumSongSortOrder(): String {
        return preferences.albumSongSortOrder ?: SortOrder.AlbumSongSortOrder.SONG_A_Z
    }

    override fun observeAlbumSongSortOrder(): Flow<String> {
        return dataStore.getAlbumSongSortOrder()
    }

    override suspend fun setArtistSongOrder(sortOrder: String) {
        preferences.artistSongSortOrder = sortOrder
        dataStore.setArtistSongSortOrder(sortOrder)
    }

    override fun getArtistSongSortOrder(): String {
        return preferences.artistSongSortOrder ?: SortOrder.ArtistSongSortOrder.SONG_A_Z
    }

    override fun observeArtistSongOrder(): Flow<String> {
        return dataStore.getArtistSongSongOrder()
    }

    override suspend fun setSongSortOrder(sortOrder: String) {
        preferences.songSortOrder = sortOrder
        dataStore.setSongSortOrder(sortOrder)
    }

    override fun getSongSortOrder(): String {
        return preferences.songSortOrder ?: SortOrder.SongSortOrder.SONG_A_Z
    }

    override fun observeSongSortOrder(): Flow<String> {
        return dataStore.getSongSortOrder()
    }

    override suspend fun setAlbumSortOrder(sortOrder: String) {
        preferences.albumSortOrder = sortOrder
        dataStore.setAlbumSortOrder(sortOrder)
    }

    override fun getAlbumSortOrder(): String {
        return preferences.albumSortOrder ?: SortOrder.AlbumSortOrder.ALBUM_A_Z
    }

    override fun observeAlbumSortOrder(): Flow<String> {
        return dataStore.getAlbumSortOrder()
    }

    override suspend fun setArtistSortOrder(sortOrder: String) {
        preferences.artistSortOrder = sortOrder
        dataStore.setArtistSortOrder(sortOrder)
    }

    override fun getArtistSortOrder(): String {
        return preferences.artistSortOrder ?: SortOrder.ArtistSortOrder.ARTIST_A_Z
    }

    override fun observeArtistSortOrder(): Flow<String> {
        return dataStore.getArtistSortOrder()
    }

    override suspend fun setPlaylistSortOrder(sortOrder: String) {
        preferences.playlistSortOrder = sortOrder
        dataStore.setPlaylistSortOrder(sortOrder)
    }

    override fun getPlaylistSortOrder(): String {
        return preferences.playlistSortOrder ?: SortOrder.PlaylistSortOrder.DEFAULT
    }

    override fun observePlaylistSortOrder(): Flow<String> {
        return dataStore.getPlaylistSortOrder()
    }
}