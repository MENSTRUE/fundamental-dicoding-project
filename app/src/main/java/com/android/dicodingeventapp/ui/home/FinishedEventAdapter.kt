package com.android.dicodingeventapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.model.Event
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class FinishedEventAdapter(
    private val onItemClick: (Event) -> Unit
) : ListAdapter<Event, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    var isLoading = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_DATA = 1
        private const val SHIMMER_ITEM_COUNT = 5

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_DATA
    }

    override fun getItemCount(): Int {
        return if (isLoading) SHIMMER_ITEM_COUNT else super.getItemCount()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shimmer_event_finish, parent, false)
            ShimmerViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finishing_event, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder && !isLoading) {
            holder.bind(getItem(position))
        }
        // Tidak perlu bind data untuk shimmer
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivEvent: ImageView = itemView.findViewById(R.id.ivEvent)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_event_category)
        private val tvBeginTime: TextView = itemView.findViewById(R.id.tvBeginTime)
        private val tvEndTime: TextView = itemView.findViewById(R.id.tvEndTime)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvOwner: TextView = itemView.findViewById(R.id.tvOwner)

        fun bind(event: Event) {
            tvTitle.text = event.name
            tvCategory.text = event.category
            tvBeginTime.text = event.beginTime
            tvEndTime.text = event.endTime
            tvLocation.text = event.cityName
            tvOwner.text = event.ownerName

            Glide.with(itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.eror_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivEvent)

            itemView.setOnClickListener { onItemClick(event) }
        }
    }

    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
