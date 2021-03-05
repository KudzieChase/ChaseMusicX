package com.chase.kudzie.chasemusic.ui.songs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.databinding.FragmentMediaHomeBinding
import com.chase.kudzie.chasemusic.domain.model.MediaIdCategory
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.chase.kudzie.chasemusic.media.IMediaProvider
import dagger.android.support.AndroidSupportInjection
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SongsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mediaProvider: IMediaProvider

    private val viewModel: SongViewModel by viewModels {
        viewModelFactory
    }

    private var _binding: FragmentMediaHomeBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaProvider = this.activity as IMediaProvider
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
            viewModel.songs.observe(
                viewLifecycleOwner, Observer { songs ->
                    mediaList.apply {
                        layoutManager = LinearLayoutManager(requireContext())

                        adapter = SongAdapter(::onSongClicked).apply {
                            submitList(songs)
                        }

                        val unwrappedDrawable: Drawable? =
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.afs_md2_thumb
                            )
                        val wrappedDrawable: Drawable = DrawableCompat.wrap(unwrappedDrawable!!)
                        DrawableCompat.setTint(wrappedDrawable, Color.CYAN)

                        FastScrollerBuilder(this).useMd2Style().setThumbDrawable(wrappedDrawable)
                            .build()
                    }
                }
            )

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_search -> {
                        val searchView =
                            binding.toolbar.findViewById<View>(R.id.menu_search)
                        ViewCompat.setTransitionName(searchView, "search_to_back")
                        val extras =
                            FragmentNavigatorExtras(searchView to "search_to_back")

                        findNavController().navigate(
                            R.id.action_songs_to_searchFragment,
                            null,
                            null,
                            extras
                        )
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }

            val searchView =
                binding.toolbar.findViewById<View>(R.id.menu_search)
            postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
            ViewCompat.setTransitionName(searchView, "search_to_back")
            view.doOnPreDraw { startPostponedEnterTransition() }
        }

    }

    private fun onSongClicked(song: Song) {
        mediaProvider.playMediaFromId(MediaIdCategory.makeSongsCategory(song.id))
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}