package com.android.dicodingeventapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.dicodingeventapp.databinding.ActivityMainBinding
import com.android.dicodingeventapp.ui.finish.FinishFragment
import com.android.dicodingeventapp.ui.home.HomeFragment
import com.android.dicodingeventapp.ui.profile.ProfileFragment
import com.android.dicodingeventapp.ui.search.SearchFragment
import com.android.dicodingeventapp.ui.upcoming.UpcomingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default fragment
        loadFragment(HomeFragment())

        // deklarasi fragment
        binding.bottomNavigation.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment())
                R.id.navigation_upcoming -> loadFragment(UpcomingFragment())
                R.id.navigation_finish -> loadFragment(FinishFragment())
                R.id.navigation_profile -> loadFragment(ProfileFragment())
                R.id.navigation_search -> loadFragment(SearchFragment())
            }
            true
        }

        }

    // load fragment
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, fragment)
            .commit()
    }

    }


