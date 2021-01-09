package com.chase.kudzie.chasemusic.data.loaders

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.BaseColumns._ID
import android.provider.MediaStore
import android.provider.MediaStore.Audio.AudioColumns.*
import android.provider.MediaStore.MediaColumns.DURATION
import android.provider.MediaStore.MediaColumns.TITLE
import com.chase.kudzie.chasemusic.data.sorting.SortOrder

internal class SongLoader(
    private val contentResolver: ContentResolver,
    private val sortOrder: String
) {

    companion object {
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            _ID,
            TITLE,
            ARTIST,
            ALBUM,
            DURATION,
            TRACK,
            ARTIST_ID,
            ALBUM_ID,
            IS_MUSIC
        )
    }

    @SuppressLint("Recycle")
    fun getAll(): Cursor {
        val selection = null
        val selectionArgs = null

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)!!
    }

    @SuppressLint("Recycle")
    fun getSong(id: Long): Cursor {
        val selection = "$_ID=?"
        val selectionArgs = arrayOf(
            "$id"
        )

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)!!
    }


    @SuppressLint("Recycle")
    fun findSongs(searchString: String): Cursor {
        val selection = "$TITLE LIKE ?"
        val selectionArgs = arrayOf(
            "%$searchString%"
        )

        return contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)!!
    }
}