package com.chase.kudzie.chasemusic.data.repository

import android.content.ContentResolver
import android.content.Context
import com.chase.kudzie.chasemusic.data.extensions.queryAll
import com.chase.kudzie.chasemusic.data.extensions.queryOne
import com.chase.kudzie.chasemusic.data.loaders.AlbumLoader
import com.chase.kudzie.chasemusic.data.loaders.ArtistLoader
import com.chase.kudzie.chasemusic.data.loaders.PlaylistLoader
import com.chase.kudzie.chasemusic.data.loaders.SongLoader
import com.chase.kudzie.chasemusic.data.mapper.toPlaylistSong
import com.chase.kudzie.chasemusic.data.mapper.toSong
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import com.chase.kudzie.chasemusic.domain.repository.SongRepository
import com.chase.kudzie.chasemusic.domain.scope.ApplicationContext
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val preferencesRepository: PreferencesRepository
) : SongRepository {
    private val contentResolver: ContentResolver = context.contentResolver

    override suspend fun getSongs(): List<Song> {
        return contentResolver.queryAll(
            SongLoader(
                contentResolver = contentResolver,
                sortOrder = preferencesRepository.getSongSortOrder()
            ).getAll()
        ) { it.toSong() }
    }

    override suspend fun findSongs(searchString: String): List<Song> {
        return contentResolver.queryAll(
            SongLoader(
                contentResolver = contentResolver,
                sortOrder = preferencesRepository.getSongSortOrder()
            ).findSongs(searchString)
        ) { it.toSong() }
    }

    override suspend fun getSong(id: Long): Song {
        return contentResolver.queryOne(
            SongLoader(
                contentResolver = contentResolver,
                sortOrder = preferencesRepository.getSongSortOrder()
            ).getSong(id)
        ) { it.toSong() }!!
    }

    override suspend fun getSongsByAlbum(albumId: Long): List<Song> {
        return contentResolver.queryAll(
            AlbumLoader(
                contentResolver = contentResolver,
                albumSortOrder = preferencesRepository.getAlbumSortOrder(),
                albumSongSortOrder = preferencesRepository.getAlbumSongSortOrder()
            ).getAlbumSongs(albumId)
        ) { it.toSong() }
    }

    override suspend fun getSongsByArtist(artistId: Long): List<Song> {
        return contentResolver.queryAll(
            ArtistLoader(
                contentResolver = contentResolver,
                artistSortOrder = preferencesRepository.getArtistSortOrder(),
                artistSongSortOrder = preferencesRepository.getArtistSongSortOrder()
            ).getArtistSongs(artistId)
        ) { it.toSong() }
    }

    override suspend fun getSongsByPlaylist(playlistId: Long): List<Song> {
        return contentResolver.queryAll(
            PlaylistLoader(
                contentResolver = contentResolver,
                playlistSongOrder = preferencesRepository.getPlaylistSortOrder()
            ).getPlaylistSongs(playlistId)
        ) { it.toPlaylistSong() }
    }

    override suspend fun deleteSong(id: Long) {
        TODO("research song deletion via content provider?")
    }
}