package com.android.dicodingeventapp.ui.upcoming

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android.dicodingeventapp.R
import com.android.dicodingeventapp.databinding.FragmentUpcomingBinding
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) { events ->
            Log.d("UpcomingFragment", "Events masuk: $events")
            events?.forEach{ event ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_finishing_event,
                    binding.linearLayoutUpcoming,
                    false
                )

                val ivEvent = itemView.findViewById<ImageView>(R.id.ivEvent)
                val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
                val tvCategory = itemView.findViewById<TextView>(R.id.tv_event_category)
                val tvBeginTime = itemView.findViewById<TextView>(R.id.tvBeginTime)
                val tvEndTime = itemView.findViewById<TextView>(R.id.tvEndTime)
                val tvLocation = itemView.findViewById<TextView>(R.id.tvLocation)
                val tvOwner = itemView.findViewById<TextView>(R.id.tvOwner)

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

                binding.linearLayoutUpcoming.addView(itemView)
            }
        }
    }


    }
