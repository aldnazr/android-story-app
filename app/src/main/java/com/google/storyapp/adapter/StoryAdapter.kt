package com.google.storyapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.storyapp.databinding.ItemStoryBinding
import com.google.storyapp.remote.response.Story
import com.google.storyapp.ui.DetailStoryActivity

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            with(binding) {
                Glide.with(itemView).load(story.photoUrl).into(ivItemPhoto)
                tvItemName.text = story.name
                ivHaveLocation.visibility =
                    if (story.lat == 0.0 && story.lon == 0.0) View.GONE else View.VISIBLE

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java).apply {
                        putExtra(DetailStoryActivity.USERNAME, story.name)
                        putExtra(DetailStoryActivity.DESCRIPTION, story.description)
                        putExtra(DetailStoryActivity.PHOTO, story.photoUrl)
                        putExtra(DetailStoryActivity.LAT, story.lat)
                        putExtra(DetailStoryActivity.LON, story.lon)
                    }
                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}