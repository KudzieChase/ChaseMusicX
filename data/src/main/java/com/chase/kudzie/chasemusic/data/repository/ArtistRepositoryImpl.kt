package com.chase.kudzie.chasemusic.data.repository

import android.content.ContentResolver
import android.content.Context
import com.chase.kudzie.chasemusic.data.extensions.queryAll
import com.chase.kudzie.chasemusic.data.extensions.queryOne
import com.chase.kudzie.chasemusic.data.loaders.ArtistLoader
import com.chase.kudzie.chasemusic.data.mapper.toArtist
import com.chase.kudzie.chasemusic.domain.model.Artist
import com.chase.kudzie.chasemusic.domain.repository.ArtistRepository
import com.chase.kudzie.chasemusic.domain.repository.PreferencesRepository
import com.chase.kudzie.chasemusic.domain.scope.ApplicationContext
import javax.inject.Inject

class ArtistRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    preferencesRepository: PreferencesRepository
) : ArtistRepository {

    private val contentResolver: ContentResolver = context.contentResolver

    private val artistLoader = ArtistLoader(
        contentResolver = contentResolver,
        artistSortOrder = preferencesRepository.getArtistSortOrder(),
        artistSongSortOrder = preferencesRepository.getArtistSongSortOrder()
    )

    override suspend fun getArtist(id: Long): Artist {
        return contentResolver.queryOne(
            artistLoader.getArtist(id)
        ) { it.toArtist() }!!
    }

    override suspend fun getArtists(): List<Artist> {
        return contentResolver.queryAll(
            artistLoader.getAll()
        ) { it.toArtist() }
    }

    override suspend fun findArtists(searchString: String): List<Artist> {
        return contentResolver.queryAll(
            artistLoader.findArtists(searchString)
        ) { it.toArtist() }
    }
}
