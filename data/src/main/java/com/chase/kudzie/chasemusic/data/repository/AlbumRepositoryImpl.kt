package com.chase.kudzie.chasemusic.data.repository

import android.content.ContentResolver
import android.content.Context
import com.chase.kudzie.chasemusic.data.extensions.queryAll
import com.chase.kudzie.chasemusic.data.extensions.queryOne
import com.chase.kudzie.chasemusic.data.loaders.AlbumLoader
import com.chase.kudzie.chasemusic.data.loaders.ArtistLoader
import com.chase.kudzie.chasemusic.data.mapper.toAlbum
import com.chase.kudzie.chasemusic.domain.model.Album
import com.chase.kudzie.chasemusic.domain.repository.AlbumRepository
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import com.chase.kudzie.chasemusic.domain.scope.ApplicationContext
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val preferencesRepository: PreferencesRepository
) : AlbumRepository {
    private val contentResolver: ContentResolver = context.contentResolver

    private val albumLoader = AlbumLoader(
        contentResolver = contentResolver,
        albumSortOrder = preferencesRepository.getAlbumSortOrder(),
        albumSongSortOrder = preferencesRepository.getAlbumSongSortOrder()
    )

    override suspend fun getAlbums(): List<Album> {
        return contentResolver.queryAll(
            albumLoader.getAll()
        ) { it.toAlbum() }
    }

    override suspend fun getAlbum(id: Long): Album {
        return contentResolver.queryOne(
            albumLoader.getAlbum(id)
        ) { it.toAlbum() }!!
    }

    override suspend fun findAlbums(searchString: String): List<Album> {
        return contentResolver.queryAll(
            albumLoader.findAlbums(searchString)
        ) { it.toAlbum() }
    }

    override suspend fun deleteAlbum(id: Long) {
        TODO("Research on multi and single deletes")
    }

    override suspend fun getAlbumsByArtist(artistId: Long): List<Album> {
        return contentResolver.queryAll(
            ArtistLoader(
                contentResolver = contentResolver,
                artistSongSortOrder = preferencesRepository.getArtistSongSortOrder(),
                artistSortOrder = preferencesRepository.getArtistSortOrder()
            ).getArtistAlbums(artistId)
        ) { it.toAlbum() }
    }
}