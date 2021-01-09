package com.chase.kudzie.chasemusic.ui.albumdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.songs.GetSongsByAlbum
import com.chase.kudzie.chasemusic.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumDetailsViewModel @Inject constructor(
    private val retrieveSongsByAlbum: GetSongsByAlbum
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

    fun getSongsByAlbum(albumId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _songs.value = retrieveSongsByAlbum(albumId = albumId)
            }
        }
    }
}