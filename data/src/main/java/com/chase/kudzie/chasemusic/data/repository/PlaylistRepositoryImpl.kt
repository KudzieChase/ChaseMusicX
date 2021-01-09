package com.chase.kudzie.chasemusic.data.repository

import android.content.ContentResolver
import android.content.Context
import com.chase.kudzie.chasemusic.data.extensions.queryAll
import com.chase.kudzie.chasemusic.data.extensions.queryCountRow
import com.chase.kudzie.chasemusic.data.loaders.PlaylistLoader
import com.chase.kudzie.chasemusic.data.mapper.toPlaylist
import com.chase.kudzie.chasemusic.domain.model.Playlist
import com.chase.kudzie.chasemusic.domain.repository.PlaylistRepository
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import com.chase.kudzie.chasemusic.domain.scope.ApplicationContext
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val preferencesRepository: PreferencesRepository
) : PlaylistRepository {

    private val contentResolver: ContentResolver = context.contentResolver

    override suspend fun getPlaylists(): List<Playlist> {
        return contentResolver.queryAll(
            PlaylistLoader(
                contentResolver = contentResolver,
                playlistSongOrder = preferencesRepository.getPlaylistSortOrder()
            ).getAll()
        ) { it.toPlaylist() }.map { playlist ->
            playlist.copy(
                songCount = contentResolver
                    .queryCountRow(
                        PlaylistLoader(
                            contentResolver = contentResolver,
                            playlistSongOrder = preferencesRepository.getPlaylistSortOrder()
                        ).getPlaylistCount(playlist.id)
                    )
            )
        }
    }

    override suspend fun deletePlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun makePlaylist() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}