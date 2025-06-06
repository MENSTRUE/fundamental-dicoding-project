package com.android.dicodingeventapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.dicodingeventapp.databinding.ActivityMainBinding
import com.android.dicodingeventapp.ui.favorite.FavoriteActivity
import com.android.dicodingeventapp.ui.finish.FinishFragment
import com.android.dicodingeventapp.ui.home.HomeFragment
import com.android.dicodingeventapp.ui.profile.ProfileFragment
import com.android.dicodingeventapp.ui.search.SearchFragment
import com.android.dicodingeventapp.ui.settings.SettingsActivity
import com.android.dicodingeventapp.ui.upcoming.UpcomingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), "Dicoding Event App") // hanya kalau pertama kali, bukan recreate
        }

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> loadFragment(HomeFragment(), "Dicoding Event App")
                R.id.navigation_upcoming -> loadFragment(UpcomingFragment(), "Upcoming")
                R.id.navigation_finish -> loadFragment(FinishFragment(), "Finish")
                R.id.navigation_profile -> loadFragment(ProfileFragment(), "Profile")
                R.id.navigation_search -> loadFragment(SearchFragment(), "Search")
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu) // sesuaikan nama file menu xml-mu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.navigation_favorite -> { // Menambahkan navigasi untuk FavoriteActivity di menu toolbar
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_fragment, fragment)
            .commit()
        supportActionBar?.title = title
    }
}
