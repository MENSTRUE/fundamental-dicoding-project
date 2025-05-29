import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.data.model.Event
import com.bumptech.glide.Glide

class FinishEventAdapter(
    private var events: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoading = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_DATA = 1
        private const val SHIMMER_ITEM_COUNT = 5
    }

    // ViewHolder untuk data event
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivEvent: ImageView = itemView.findViewById(R.id.ivEvent)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvCategory: TextView = itemView.findViewById(R.id.tv_event_category)
        val tvBeginTime: TextView = itemView.findViewById(R.id.tvBeginTime)
        val tvEndTime: TextView = itemView.findViewById(R.id.tvEndTime)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvOwner: TextView = itemView.findViewById(R.id.tvOwner)
    }

    // ViewHolder untuk shimmer loading
    inner class ShimmerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Tidak perlu bind data, shimmer cuma animasi statis
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_DATA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_shimmer_event_finish, parent, false)
            ShimmerViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_finishing_event, parent, false)
            EventViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) SHIMMER_ITEM_COUNT else events.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is EventViewHolder && !isLoading) {
            val event = events[position]
            holder.tvTitle.text = event.name
            holder.tvCategory.text = event.category
            holder.tvBeginTime.text = event.beginTime
            holder.tvEndTime.text = event.endTime
            holder.tvLocation.text = event.cityName
            holder.tvOwner.text = event.ownerName

            Glide.with(holder.itemView.context)
                .load(event.imageLogo)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.eror_image)
                .into(holder.ivEvent)

            holder.itemView.setOnClickListener {
                onClick(event)
            }
        }
        // Kalau shimmer, gak perlu binding apa-apa
    }

    fun setData(newEvents: List<Event>) {
        events = newEvents
        isLoading = false
        notifyDataSetChanged()
    }
}
