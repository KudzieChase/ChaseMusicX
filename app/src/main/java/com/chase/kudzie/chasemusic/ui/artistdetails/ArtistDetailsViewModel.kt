package com.chase.kudzie.chasemusic.ui.artistdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.albums.GetAlbumsByArtist
import com.chase.kudzie.chasemusic.domain.interactor.songs.GetSongsByArtist
import com.chase.kudzie.chasemusic.domain.model.Album
import com.chase.kudzie.chasemusic.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistDetailsViewModel @Inject constructor(
    private val retrieveAlbumsByArtist: GetAlbumsByArtist,
    private val retrieveSongsByArtist: GetSongsByArtist
) : ViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    private val _songs = MutableLiveData<List<Song>>()

    val albums: LiveData<List<Album>>
        get() = _albums
    val songs: LiveData<List<Song>>
        get() = _songs

    fun getAlbumsByArtist(artistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _albums.value = retrieveAlbumsByArtist(artistId)
            }
        }
    }

    fun getSongsByArtist(artistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _songs.value = retrieveSongsByArtist(artistId)
            }
        }
    }

}