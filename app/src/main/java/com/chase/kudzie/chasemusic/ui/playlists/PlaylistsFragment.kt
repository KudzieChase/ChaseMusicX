package com.chase.kudzie.chasemusic.ui.playlists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.chase.kudzie.chasemusic.databinding.FragmentPlaylistsBinding
import com.chase.kudzie.chasemusic.domain.model.Playlist
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.chase.kudzie.chasemusic.ui.albums.AlbumsFragmentDirections
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class PlaylistsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: PlaylistsViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.playlists.observe(
                viewLifecycleOwner, { playlists ->
                    playlistList.apply {
                        adapter = PlaylistAdapter(::onPlaylistClick).apply {
                            submitList(playlists)
                        }
                    }
                }
            )
        }
    }

    private fun onPlaylistClick(view: View, playlist: Playlist) {
        val directions =
            PlaylistsFragmentDirections.actionPlaylistsToPlaylistDetailsFragment(
                playlist.id,
                playlist.name
            )

        view.findNavController().navigate(directions)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}