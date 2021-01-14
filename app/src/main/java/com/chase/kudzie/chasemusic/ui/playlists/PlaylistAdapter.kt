package com.chase.kudzie.chasemusic.ui.playlists

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chase.kudzie.chasemusic.databinding.ItemPlaylistBinding
import com.chase.kudzie.chasemusic.domain.model.Playlist
import com.chase.kudzie.chasemusic.model.PlaylistDiff

class PlaylistAdapter(val onPlaylistClick: (View, Playlist) -> Unit) :
    ListAdapter<Playlist, PlaylistAdapter.ItemHolder>(PlaylistDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemPlaylistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val playlist = getItem(position)

        holder.bind(playlist)
    }

    inner class ItemHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(playlist: Playlist) {
            binding.run {
                this.playlist = playlist

                click(playlist)
            }
        }

        private fun click(playlist: Playlist) {
            itemView.setOnClickListener {
                onPlaylistClick(itemView, playlist)
            }
        }
    }
}