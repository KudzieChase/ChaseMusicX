package com.chase.kudzie.chasemusic.ui.playlistdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.songs.GetSongsByPlaylist
import com.chase.kudzie.chasemusic.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaylistDetailsViewModel @Inject constructor(
    private val retrieveSongsByPlaylist: GetSongsByPlaylist
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

    fun getSongsByPlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _songs.value = retrieveSongsByPlaylist(playlistId)
            }
        }
    }

}