package com.chase.kudzie.chasemusic.ui.albumdetails

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.databinding.FragmentAlbumDetailsBinding
import com.chase.kudzie.chasemusic.domain.model.MediaIdCategory
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.extensions.themeColor
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.chase.kudzie.chasemusic.media.IMediaProvider
import com.chase.kudzie.chasemusic.ui.albumdetails.adapters.DetailSongsAdapter
import com.chase.kudzie.chasemusic.ui.albums.AlbumViewModel
import com.chase.kudzie.chasemusic.ui.artists.ArtistsViewModel
import com.chase.kudzie.chasemusic.util.loadListener
import com.chase.kudzie.chasemusic.util.widget.RoundedRectDrawable
import com.google.android.material.transition.MaterialContainerTransform
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import me.saket.cascade.CascadePopupMenu
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AlbumDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val albumDetailViewModel: AlbumDetailsViewModel by viewModels {
        viewModelFactory
    }

    private val artistViewModel: ArtistsViewModel by viewModels {
        viewModelFactory
    }

    private val albumViewModel: AlbumViewModel by viewModels {
        viewModelFactory
    }

    private val args: AlbumDetailsFragmentArgs by navArgs()

    private lateinit var mediaProvider: IMediaProvider

    private var _binding: FragmentAlbumDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val albumId = args.albumId
        albumViewModel.getAlbum(albumId)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
        mediaProvider = this.activity as IMediaProvider
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlbumDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            albumViewModel.album.observe(viewLifecycleOwner, { album ->

                this.album = album
                artistViewModel.getArtist(album.artistId)
                albumDetailViewModel.getSongsByAlbum(album.id)

                this.loadListener = loadListener {
                    startPostponedEnterTransition()
                }

            })

            artistViewModel.artist.observe(viewLifecycleOwner, { artist ->
                this.artist = artist
            })

            albumDetailViewModel.songs.observe(viewLifecycleOwner, { songs ->
                songsList.apply {
                    adapter = DetailSongsAdapter(::onSongClick).apply {
                        submitList(songs)
                    }
                }
            })

            albumDetailViewModel.sortOrderFlow
                .flowOn(Dispatchers.Main)
                .asLiveData()
                .observe(viewLifecycleOwner, {
                    albumDetailViewModel.getSongsByAlbum(args.albumId)
                })

            btnShuffle.setOnClickListener {
                //TODO implement and execute shuffle
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            menuButton.setOnClickListener {
                showCascadeMenu(anchor = menuButton)
            }

            postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
            val interp = AnimationUtils.loadInterpolator(
                context,
                android.R.interpolator.fast_out_slow_in
            )

            sharedElementEnterTransition = MaterialContainerTransform().apply {
                duration = resources.getInteger(R.integer.motion_duration_large).toLong()
                interpolator = interp
                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
            }

            sharedElementReturnTransition = MaterialContainerTransform().apply {
                duration = 300L
                interpolator = interp
                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
            }

            view.doOnPreDraw { startPostponedEnterTransition() }

        }
    }


    private fun onSongClick(song: Song) {
        mediaProvider.playMediaFromId(MediaIdCategory.makeAlbumCategory(args.albumId, song.id))
    }

    private fun showCascadeMenu(anchor: View) {
        val popupMenu = CascadePopupMenu(requireContext(), anchor)

        popupMenu.menu.apply {
            add("Add to a Playlist").setOnMenuItemClickListener {
                Toast.makeText(requireContext(), "Working on it :D", Toast.LENGTH_SHORT).show()
                popupMenu.navigateBack()
                true
            }
            add("Play Next").setOnMenuItemClickListener {
                Toast.makeText(requireContext(), "Working on it :D", Toast.LENGTH_SHORT).show()
                popupMenu.navigateBack()
                true
            }
            addSubMenu("Sort by").also { submenu ->
                submenu.add("Title Ascending").setOnMenuItemClickListener {
                    albumDetailViewModel.sortOrderAZ()
                    popupMenu.navigateBack()
                    true
                }
                submenu.add("Title Descending").setOnMenuItemClickListener {
                    albumDetailViewModel.sortOrderZA()
                    popupMenu.navigateBack()
                    true
                }
                submenu.add("Track Number").setOnMenuItemClickListener {
                    albumDetailViewModel.sortOrderTrackNumber()
                    popupMenu.navigateBack()
                    true
                }
                submenu.add("Song Duration").setOnMenuItemClickListener {
                    albumDetailViewModel.sortOrderSongDuration()
                    popupMenu.navigateBack()
                    true
                }
            }
        }

        popupMenu.show()
    }

    private fun cascadeMenuStyler(): CascadePopupMenu.Styler {
        val rippleDrawable = {
            RippleDrawable(
                ColorStateList.valueOf(Color.parseColor("#B1DDC6")),
                null,
                ColorDrawable(BLACK)
            )
        }

        return CascadePopupMenu.Styler(
            background = {
                RoundedRectDrawable(Color.parseColor("#E0EEE7"), radius = 8f.dip)
            },
            menuTitle = {
                it.itemView.background = rippleDrawable()
            },
            menuItem = {
                it.itemView.background = rippleDrawable()
            }
        )
    }

    private val Float.dip: Float
        get() {
            val metrics = resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}