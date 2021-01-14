package com.chase.kudzie.chasemusic.ui.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chase.kudzie.chasemusic.databinding.ItemSongBinding
import com.chase.kudzie.chasemusic.domain.model.Song
import com.chase.kudzie.chasemusic.model.SongDiff

class SongAdapter(val onSongClick: (Song) -> Unit) :
    ListAdapter<Song, SongAdapter.ItemHolder>(SongDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemSongBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val song = getItem(position)

        holder.bind(song)
    }


    inner class ItemHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.run {
                this.song = song

                click(song)
            }
        }

        private fun click(song: Song) {
            itemView.setOnClickListener {
                onSongClick(song)
            }
        }
    }


}