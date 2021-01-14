package com.chase.kudzie.chasemusic.ui.albums

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.databinding.FragmentAlbumsBinding
import com.chase.kudzie.chasemusic.domain.model.Album
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.google.android.material.transition.MaterialElevationScale
import dagger.android.support.AndroidSupportInjection
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import javax.inject.Inject


class AlbumsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AlbumViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentAlbumsBinding? = null
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
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.albums.observe(viewLifecycleOwner, { albums ->
                albumsGrid.apply {
                    adapter = AlbumAdapter(::onAlbumClicked).apply {
                        submitList(albums)
                    }

                    FastScrollerBuilder(this).useMd2Style().build()
                }
            })

            postponeEnterTransition()
            view.doOnPreDraw { startPostponedEnterTransition() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onAlbumClicked(view: View, album: Album) {

        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.motion_duration_large).toLong()
        }

        val extras = FragmentNavigatorExtras(
            view to "album_shared_element"
        )
        val directions = AlbumsFragmentDirections.actionAlbumsToAlbumDetails(album.id)
        view.findNavController().navigate(directions, extras)
    }
}