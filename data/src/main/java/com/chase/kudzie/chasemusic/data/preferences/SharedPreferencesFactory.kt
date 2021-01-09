package com.chase.kudzie.chasemusic.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.chase.kudzie.chasemusic.data.sorting.SortOrder

object SharedPreferencesFactory {

    private const val CHASE_MUSIC_PREFERENCES = "chase_music_prefs"

    private const val CURRENT_POSITION_IN_QUEUE = "current_position_in_queue"
    private const val REPEAT_MODE = "repeat_mode"
    private const val PLAY_MODE = "play_mode"
    private const val SHUFFLE_MODE = "shuffle_mode"
    private const val CURRENT_SONG_POS = "song_duration_position"

    //Sort Order
    private const val SONG_SORT_ORDER = "song_sort_order"
    private const val ALBUM_SORT_ORDER = "album_sort_order"
    private const val ARTIST_SORT_ORDER = "artist_sort_order"
    private const val PLAYLIST_SORT_ORDER = "playlist_sort_order"
    private const val ALBUM_SONG_SORT_ORDER = "album_songs_sort_order"
    private const val ARTIST_SONG_SORT_ORDER = "artist_songs_sort_order"
    private const val PLAYLIST_SONG_SORT_ORDER = "playlist_songs_sort_order"

    fun makeChaseMusicPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(CHASE_MUSIC_PREFERENCES, Context.MODE_PRIVATE)
    }

    var SharedPreferences.currentPositionInQueue: Int
        get() = getInt(CURRENT_POSITION_IN_QUEUE, 0)
        set(value) {
            edit {
                putInt(CURRENT_POSITION_IN_QUEUE, value)
            }
        }

    var SharedPreferences.repeatMode: Int
        get() = getInt(REPEAT_MODE, 0)
        set(value) {
            edit {
                putInt(REPEAT_MODE, value)
            }
        }

    var SharedPreferences.playMode: Int
        get() = getInt(PLAY_MODE, 0)
        set(value) {
            edit {
                putInt(PLAY_MODE, value)
            }
        }

    var SharedPreferences.shuffleMode: Int
        get() = getInt(SHUFFLE_MODE, 0)
        set(value) {
            edit {
                putInt(SHUFFLE_MODE, value)
            }
        }

    var SharedPreferences.currentSongDurationPos: Long
        get() = getLong(CURRENT_SONG_POS, 0L)
        set(value) {
            edit {
                putLong(CURRENT_SONG_POS, value)
            }
        }

    var SharedPreferences.albumSongSortOrder: String?
        get() = getString(ALBUM_SONG_SORT_ORDER, SortOrder.AlbumSongSortOrder.SONG_A_Z)
        set(value) {
            edit {
                putString(ALBUM_SONG_SORT_ORDER, value)
            }
        }

    var SharedPreferences.artistSongSortOrder: String?
        get() = getString(ARTIST_SONG_SORT_ORDER, SortOrder.ArtistSongSortOrder.SONG_A_Z)
        set(value) {
            edit {
                putString(ARTIST_SONG_SORT_ORDER, value)
            }
        }

    var SharedPreferences.songSortOrder: String?
        get() = getString(SONG_SORT_ORDER, SortOrder.SongSortOrder.SONG_A_Z)
        set(value) {
            edit {
                putString(SONG_SORT_ORDER, value)
            }
        }

    var SharedPreferences.albumSortOrder: String?
        get() = getString(ALBUM_SORT_ORDER, SortOrder.AlbumSortOrder.ALBUM_A_Z)
        set(value) {
            edit {
                putString(ALBUM_SORT_ORDER, value)
            }
        }

    var SharedPreferences.artistSortOrder: String?
        get() = getString(ARTIST_SORT_ORDER, SortOrder.ArtistSortOrder.ARTIST_A_Z)
        set(value) {
            edit {
                putString(ARTIST_SORT_ORDER, value)
            }
        }

    var SharedPreferences.playlistSortOrder: String?
        get() = getString(PLAYLIST_SORT_ORDER, SortOrder.PlaylistSortOrder.DEFAULT)
        set(value) {
            edit {
                putString(PLAYLIST_SORT_ORDER, value)
            }
        }
}