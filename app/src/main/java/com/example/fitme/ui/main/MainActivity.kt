package com.example.fitme.ui.main

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(){

    private var navController: NavController? = null

    override val viewModel: MainViewModel by viewModel()

    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.homeFragment -> {
                window.statusBarColor = fetchColor(R.color.white)
                binding.navView.visible = true
            }
            else -> {
                window.statusBarColor = fetchColor(R.color.white)
                binding.navView.visible = true
            }
        }
    }

    override fun initView() {
        super.initView()

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        this.navController = findNavController(R.id.nav_host_fragment)
        binding.navView.setupWithNavController(navController!!)
        binding.navView.itemIconTintList = null
    }

    override fun onResume() {
        super.onResume()
        navController?.addOnDestinationChangedListener(navListener)
    }

    override fun onPause() {
        navController?.removeOnDestinationChangedListener(navListener)
        super.onPause()
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }
}