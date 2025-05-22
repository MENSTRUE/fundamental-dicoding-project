package com.android.dicodingeventapp.ui.home

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
import com.android.dicodingeventapp.databinding.FragmentHomeBinding
import com.android.dicodingeventapp.ui.viewmodel.EventViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // deklarasi disini dulu
//    @Inject
//    lateinit var viewModel: EventViewModel

    private val viewModel: EventViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // done
        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) { events ->
            Log.d("HomeFragment", "Events masuk: $events")
            events?.forEach { event ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_upcomming_event,
                    binding.linearLayoutSlider,
                    false
                )

                val imageView = itemView.findViewById<ImageView>(R.id.upcomming_event)
                val textView = itemView.findViewById<TextView>(R.id.tv_upcomming)

                textView.text = event.name
                val imageUrl = event.imageLogo


                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.eror_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)



                binding.linearLayoutSlider.addView(itemView)
            }
        }

        //progress
        viewModel.getFinishedEvent().observe(viewLifecycleOwner) { events ->
            Log.d("HomeFragment", "Finish Events masuk: $events")


            events?.forEach{ event ->
                val itemView = layoutInflater.inflate(
                    R.layout.item_finishing_event,
                    binding.linearLayoutSlider,
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

                binding.linearLayoutSlider.addView(itemView)

            }

        }

    }



//        viewModel.getUpcomingEvent().observe(viewLifecycleOwner) { events ->
//            Log.d("HomeFragment", "Events masuk: $events")
//            if (events.isNullOrEmpty()) {
//                Log.d("HomeFragment", "Tidak ada event yang diterima")
//            } else {
//                events.forEach { event ->
//                    Log.d("HomeFragment", "Event: ${event.name}")
//                }
//            }
//        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
