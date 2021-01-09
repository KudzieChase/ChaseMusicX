package com.chase.kudzie.chasemusic.ui.albumdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.data.sorting.SortOrder
import com.chase.kudzie.chasemusic.domain.interactor.songs.GetSongsByAlbum
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumDetailsViewModel @Inject constructor(
    private val retrieveSongsByAlbum: GetSongsByAlbum,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

    val sortOrderFlow: Flow<String> = preferencesRepository.observeAlbumSongSortOrder()

    fun getSongsByAlbum(albumId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _songs.value = retrieveSongsByAlbum(albumId = albumId)
            }
        }
    }

    fun sortOrderAZ() {
        viewModelScope.launch {
            preferencesRepository.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z)
        }
    }

    fun sortOrderZA() {
        viewModelScope.launch {
            preferencesRepository.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A)
        }
    }

    fun sortOrderTrackNumber() {
        viewModelScope.launch {
            preferencesRepository.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_TRACK_LIST)
        }
    }

    fun sortOrderSongDuration() {
        viewModelScope.launch {
            preferencesRepository.setAlbumSongSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION)
        }
    }

}