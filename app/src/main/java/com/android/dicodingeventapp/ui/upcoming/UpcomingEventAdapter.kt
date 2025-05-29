package com.android.dicodingeventapp.ui.upcoming

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.model.Event
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class UpcomingEventAdapter(
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val events = mutableListOf<Event>()
    var isLoading = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun submitList(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if (isLoading) 5 else events.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_EVENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val view = inflater.inflate(R.layout.item_shimmer_event_finish, parent, false)
            ShimmerViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_finishing_event, parent, false)
            EventViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder && !isLoading) {
            val event = events[position]
            holder.bind(event)
        }
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivEvent = itemView.findViewById<ImageView>(R.id.ivEvent)
        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvCategory = itemView.findViewById<TextView>(R.id.tv_event_category)
        private val tvBeginTime = itemView.findViewById<TextView>(R.id.tvBeginTime)
        private val tvEndTime = itemView.findViewById<TextView>(R.id.tvEndTime)
        private val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
        private val tvOwner = itemView.findViewById<TextView>(R.id.tvOwner)

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

    companion object {
        private const val VIEW_TYPE_EVENT = 0
        private const val VIEW_TYPE_SHIMMER = 1
    }
}
