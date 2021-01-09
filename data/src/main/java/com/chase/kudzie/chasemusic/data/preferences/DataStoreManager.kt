package com.chase.kudzie.chasemusic.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import com.chase.kudzie.chasemusic.data.sorting.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val QUEUE_POSITION = preferencesKey<Int>("current_queue_position")
        private val ALBUM_SONG_SORT_ORDER = preferencesKey<String>("sort_order_album_songs")
        private val ARTIST_SONG_SORT_ORDER = preferencesKey<String>("sort_order_artist_songs")
        private val SONG_SORT_ORDER = preferencesKey<String>("sort_order_song")
        private val ALBUM_SORT_ORDER = preferencesKey<String>("sort_order_album")
        private val ARTIST_SORT_ORDER = preferencesKey<String>("sort_order_artist")
        private val PLAYLIST_SORT_ORDER = preferencesKey<String>("sort_order_playlist")
        private val PLAYLIST_SONG_SORT_ORDER = preferencesKey<String>("sort_order_playlist_songs")
    }

    suspend fun setCurrentQueuePosition(currentPosition: Int) {
        dataStore.edit { preferences ->
            preferences[QUEUE_POSITION] = currentPosition
        }
    }

    fun getCurrentQueuePosition(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[QUEUE_POSITION] ?: 0
        }
    }

    suspend fun setAlbumSongSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[ALBUM_SONG_SORT_ORDER] = sortOrder
        }
    }

    fun getAlbumSongSortOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ALBUM_SONG_SORT_ORDER] ?: SortOrder.AlbumSongSortOrder.SONG_A_Z
        }
    }

    suspend fun setArtistSongSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[ARTIST_SONG_SORT_ORDER] = sortOrder
        }
    }

    fun getArtistSongSongOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ARTIST_SONG_SORT_ORDER] ?: SortOrder.ArtistSongSortOrder.SONG_A_Z
        }
    }

    suspend fun setSongSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[SONG_SORT_ORDER] = sortOrder
        }
    }

    fun getSongSortOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SONG_SORT_ORDER] ?: SortOrder.SongSortOrder.SONG_A_Z
        }
    }

    suspend fun setAlbumSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[ALBUM_SORT_ORDER] = sortOrder
        }
    }

    fun getAlbumSortOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ALBUM_SORT_ORDER] ?: SortOrder.AlbumSortOrder.ALBUM_A_Z
        }
    }

    suspend fun setArtistSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[ARTIST_SORT_ORDER] = sortOrder
        }
    }

    fun getArtistSortOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ARTIST_SORT_ORDER] ?: SortOrder.ArtistSortOrder.ARTIST_A_Z
        }
    }

    suspend fun setPlaylistSortOrder(sortOrder: String) {
        dataStore.edit { preferences ->
            preferences[PLAYLIST_SORT_ORDER] = sortOrder
        }
    }

    fun getPlaylistSortOrder(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[PLAYLIST_SORT_ORDER] ?: SortOrder.PlaylistSortOrder.DEFAULT
        }
    }
}