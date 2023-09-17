package com.kepler.codefliesassignment

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kepler.codefliesassignment.databinding.ActivityMainBinding

class MainActivity : BaseActivity(), NavController.OnDestinationChangedListener {

    lateinit var binding: ActivityMainBinding

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)


        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(this)

    }


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.commentViewFragment -> {
                binding.bottomNavigationView.visibility = View.GONE
                binding.imageViewAddIcon.visibility = View.GONE
            }

            else -> {
                binding.bottomNavigationView.visibility = View.VISIBLE
                binding.imageViewAddIcon.visibility = View.VISIBLE

            }
        }


    }


}