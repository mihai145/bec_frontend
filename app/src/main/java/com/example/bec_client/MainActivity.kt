package com.example.bec_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapi.home.viewmodel.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment(); val profileFragment = ProfileFragment(); val searchFragment = SearchFragment(); val trendingFragment = TrendingFragment(); val nearbyFragment = NearbyFragment()
        setCurrentFragment(homeFragment)
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.trending -> setCurrentFragment(trendingFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.nearby -> setCurrentFragment(nearbyFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragment)
            commit()
        }
    }
}