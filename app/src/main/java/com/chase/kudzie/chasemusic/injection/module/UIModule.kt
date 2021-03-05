package com.chase.kudzie.chasemusic.injection.module

import com.chase.kudzie.chasemusic.MainActivity
import com.chase.kudzie.chasemusic.ui.albums.AlbumsFragment
import com.chase.kudzie.chasemusic.ui.albumdetails.AlbumDetailsFragment
import com.chase.kudzie.chasemusic.ui.artistdetails.ArtistDetailsFragment
import com.chase.kudzie.chasemusic.ui.artists.ArtistsFragment
import com.chase.kudzie.chasemusic.ui.nowplaying.PlayerFragment
import com.chase.kudzie.chasemusic.ui.nowplaying.PlayerMiniFragment
import com.chase.kudzie.chasemusic.ui.playlists.PlaylistsFragment
import com.chase.kudzie.chasemusic.ui.playlistdetails.PlaylistDetailsFragment
import com.chase.kudzie.chasemusic.ui.queue.PlayingQueueActivity
import com.chase.kudzie.chasemusic.ui.search.SearchFragment
import com.chase.kudzie.chasemusic.ui.songs.SongsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UIModule {
    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributesPlayingQueueActivity(): PlayingQueueActivity
}

@Module
abstract class FragmentScreenModule {
    @ContributesAndroidInjector
    abstract fun contributesAlbumsFragment(): AlbumsFragment

    @ContributesAndroidInjector
    abstract fun contributesSongsFragment(): SongsFragment

    @ContributesAndroidInjector
    abstract fun contributesArtistsFragment(): ArtistsFragment

    @ContributesAndroidInjector
    abstract fun contributesPlayerFragment(): PlayerFragment

    @ContributesAndroidInjector
    abstract fun contributesMiniPlayerFragment(): PlayerMiniFragment

    @ContributesAndroidInjector
    abstract fun contributesPlaylistsFragment(): PlaylistsFragment

    @ContributesAndroidInjector
    abstract fun contributesAlbumSongsFragment(): AlbumDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributesArtistDetailsFragment(): ArtistDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributesPlaylistSongsFragment(): PlaylistDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributesSearchFragment(): SearchFragment
}