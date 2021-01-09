package com.chase.kudzie.chasemusic.ui.queue

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chase.kudzie.chasemusic.R
import com.chase.kudzie.chasemusic.base.BaseMediaActivity
import com.chase.kudzie.chasemusic.databinding.ActivityPlayingQueueBinding
import com.chase.kudzie.chasemusic.injection.ViewModelFactory
import com.chase.kudzie.chasemusic.media.model.PlayableMediaItem
import com.chase.kudzie.chasemusic.util.bindingadapters.contentView
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PlayingQueueActivity : BaseMediaActivity() {

    private val binding: ActivityPlayingQueueBinding by contentView(R.layout.activity_playing_queue)

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var mQueue: List<PlayableMediaItem> = listOf()

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: QueueViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding.apply {

            observePlayingQueue()
                .flowOn(Dispatchers.Default)
                .asLiveData().observe(this@PlayingQueueActivity, { queue ->

                    mQueue = queue

                    songsList.apply {
                        adapter = PlayingQueueAdapter(
                            ::onQueueItemClicked,
                            ::startDragging
                        ).apply {
                            submitList(queue)
                        }

                        itemTouchHelper = DragTouchHelper(
                            queue,
                            ::onItemDragged
                        ).itemTouchHelper

                        itemTouchHelper.attachToRecyclerView(this)

                        scrollToPosition(viewModel.currentQueuePosition)
                    }
                })

            viewModel.currentQueuePositionFlow.flowOn(Dispatchers.Default)
                .asLiveData()
                .observe(this@PlayingQueueActivity, { positionInQueue ->
                    scrollToPosition(positionInQueue)
                })

            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun scrollToPosition(currentQueuePosition: Int) {
        if (mQueue.isNotEmpty()) {
            val position =
                mQueue.indexOfFirst { song -> song.positionInQueue == currentQueuePosition.toLong() }

            if (position != -1) {
                this.binding.songsList.stopScroll()
                (this.binding.songsList.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(
                        position,
                        0
                    )
            }
        }
    }

    private fun onItemDragged(from: Int, to: Int) {
        swap(from, to)
    }

    private fun startDragging(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    private fun onQueueItemClicked(view: View, item: PlayableMediaItem) {
        skipToQueueItem(item.positionInQueue)
    }

}