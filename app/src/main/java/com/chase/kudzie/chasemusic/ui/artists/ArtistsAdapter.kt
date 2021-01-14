package com.chase.kudzie.chasemusic.ui.artists

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chase.kudzie.chasemusic.databinding.ItemArtistBinding
import com.chase.kudzie.chasemusic.domain.model.Artist
import com.chase.kudzie.chasemusic.model.ArtistDiff

class ArtistsAdapter(
    val artistClick: (View, Artist) -> Unit,
    val context: Activity
) :
    ListAdapter<Artist, ArtistsAdapter.ItemHolder>(ArtistDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            ItemArtistBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val artist = getItem(position)

        holder.bind(artist)
    }


    inner class ItemHolder(private val binding: ItemArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(artist: Artist) {
            binding.run {
                this.artist = artist

                click(artist)
            }
        }

//        private fun simulatePlaceholder() {
//            //I will simulate a placeholder for now
//            val bitmapFromFactory =
//                BitmapFactory.decodeResource(context.resources, R.drawable.placeholder)
//            artistImage.setImageBitmap(bitmapFromFactory)
//            setGradientOnView(paletteView, bitmapFromFactory)
//        }

        fun click(artist: Artist) {
            itemView.setOnClickListener {
                artistClick(itemView, artist)
            }
        }
    }
}