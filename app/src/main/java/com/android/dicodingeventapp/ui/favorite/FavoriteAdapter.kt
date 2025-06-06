package com.android.dicodingeventapp.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.local.entity.FavoriteEventEntity
import com.android.dicodingeventapp.databinding.ItemFinishingEventBinding // Menggunakan binding ini
import com.bumptech.glide.Glide

class FavoriteAdapter(private val onClick: (FavoriteEventEntity) -> Unit) :
    ListAdapter<FavoriteEventEntity, FavoriteAdapter.FavoriteViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFinishingEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.itemView.setOnClickListener {
            onClick(event)
        }
    }

    inner class FavoriteViewHolder(private val binding: ItemFinishingEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: FavoriteEventEntity) {
            binding.tvTitle.text = event.title
            binding.tvBeginTime.text = event.startDate
            binding.tvLocation.text = event.location

            // PENTING: Memuat logoUrl, bukan imageUrl, untuk menampilkan logo
            Glide.with(binding.root.context)
                .load(event.logoUrl) // Menggunakan properti logoUrl
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.eror_image)
                .into(binding.ivEvent) // Asumsikan ID ImageView Anda adalah ivEvent untuk logo
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FavoriteEventEntity>() {
            override fun areItemsTheSame(oldItem: FavoriteEventEntity, newItem: FavoriteEventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FavoriteEventEntity, newItem: FavoriteEventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}
