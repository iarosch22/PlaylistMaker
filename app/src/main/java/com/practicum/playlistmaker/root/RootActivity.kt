package com.practicum.playlistmaker.root

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.playerFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomNavDivider.visibility = View.GONE
                }
                R.id.creationPlaylistFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomNavDivider.visibility = View.GONE
                }
                R.id.aboutPlaylistFragment -> {
                    binding.bottomNavigationView.visibility = View.GONE
                    binding.bottomNavDivider.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigationView.visibility = View.VISIBLE
                    binding.bottomNavDivider.visibility = View.VISIBLE
                }
            }
        }
    }
}