package com.chase.kudzie.chasemusic.ui.albums

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.albums.GetAlbum
import com.chase.kudzie.chasemusic.domain.interactor.browse.album.GetAlbums
import com.chase.kudzie.chasemusic.domain.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AlbumViewModel @Inject constructor(
    private val getAlbums: GetAlbums,
    private val _getAlbum: GetAlbum
) : ViewModel() {

    private val _albums = MutableLiveData<List<Album>>()
    val albums: LiveData<List<Album>>
        get() = _albums

    private val _album = MutableLiveData<Album>()
    val album: LiveData<Album>
        get() = _album

    init {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _albums.value = getAlbums()
            }
        }
    }

    fun getAlbum(albumId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _album.value = _getAlbum(GetAlbum.Params.forAlbum(albumId))
            }
        }
    }

}