package com.chase.kudzie.chasemusic.data.loaders

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns._ID
import android.provider.MediaStore
import android.provider.MediaStore.Audio.*

internal class PlaylistLoader(
    private val contentResolver: ContentResolver,
    private val playlistSongOrder: String
) {

    companion object {
        val uri: Uri = Playlists.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            _ID,
            PlaylistsColumns.NAME
        )

        const val selection = "${AudioColumns.IS_MUSIC} = 1 " +
                "AND ${AudioColumns.TITLE} != '' "
    }

    @SuppressLint("Recycle")
    fun getAll(): Cursor {
        val selection = null
        val selectionArgs = null

        return contentResolver.query(uri, projection, selection, selectionArgs, playlistSongOrder)!!
    }

    @SuppressLint("Recycle")
    fun getPlaylistCount(playlistId: Long): Cursor {
        val uri: Uri = Playlists.Members.getContentUri("external", playlistId)
        return contentResolver.query(uri, arrayOf(_ID), selection, null, null)!!
    }

    @SuppressLint("Recycle")
    fun getPlaylistSongs(playlistId: Long): Cursor {
        val uri: Uri = Playlists.Members.getContentUri("external", playlistId)
        val projection = arrayOf(
            _ID,
            Playlists.Members.AUDIO_ID,
            MediaStore.MediaColumns.TITLE,
            AudioColumns.ARTIST,
            AudioColumns.ALBUM,
            MediaStore.MediaColumns.DURATION,
            AudioColumns.TRACK,
            AudioColumns.ARTIST_ID,
            AudioColumns.ALBUM_ID,
            AudioColumns.IS_MUSIC
        )
        val sortOrder = Playlists.Members.DEFAULT_SORT_ORDER

        return contentResolver.query(uri, projection, selection, null, sortOrder)!!
    }
}