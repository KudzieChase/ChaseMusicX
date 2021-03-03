package com.chase.kudzie.chasemusic.ui.artists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.databinding.FragmentMediaHomeBinding
import com.chase.kudzie.chasemusic.domain.model.Artist
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.google.android.material.transition.MaterialElevationScale
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import javax.inject.Inject

class ArtistsFragment : Fragment(),
    CoroutineScope by MainScope() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ArtistsViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentMediaHomeBinding? = null
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
        _binding = FragmentMediaHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.artists.observe(
                viewLifecycleOwner, { artists ->
                    mediaList.apply {
                        layoutManager = GridLayoutManager(requireContext(), 2)
                        adapter = ArtistsAdapter(::onArtistClick, requireActivity())
                            .apply {
                                submitList(artists)
                            }
                    }
                })

            postponeEnterTransition()
            view.doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    private fun onArtistClick(view: View, artist: Artist) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }

        val extras = FragmentNavigatorExtras(
            view to "artist_shared_element"
        )

        val action = ArtistsFragmentDirections.actionArtistsToArtistDetails(artist.id)
        view.findNavController().navigate(action, extras)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}