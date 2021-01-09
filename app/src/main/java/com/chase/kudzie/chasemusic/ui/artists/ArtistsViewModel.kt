package com.chase.kudzie.chasemusic.ui.artists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chase.kudzie.chasemusic.domain.interactor.artists.GetArtist
import com.chase.kudzie.chasemusic.domain.interactor.browse.artists.GetArtists
import com.chase.kudzie.chasemusic.domain.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ArtistsViewModel @Inject constructor(
    private val getArtistsUseCase: GetArtists,
    private val getArtistUseCase: GetArtist,
) : ViewModel() {

    private val _artists = MutableLiveData<List<Artist>>()
    val artists: LiveData<List<Artist>>
        get() = _artists

    private val _artist = MutableLiveData<Artist>()
    val artist: LiveData<Artist>
        get() = _artist

    init {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _artists.value = getArtistsUseCase()
            }
        }
    }

    fun getArtist(artistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _artist.value = getArtistUseCase(GetArtist.Params.forArtist(artistId))
            }
        }
    }


}