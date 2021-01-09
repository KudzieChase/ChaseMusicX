package com.chase.kudzie.chasemusic.ui.songs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.browse.song.GetSongs
import com.chase.kudzie.chasemusic.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SongViewModel @Inject constructor(
    private val getSongs: GetSongs
) : ViewModel() {

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>>
        get() = _songs

    init {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _songs.value = getSongs()
            }
        }
    }
}