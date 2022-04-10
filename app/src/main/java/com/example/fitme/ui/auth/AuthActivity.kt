package com.example.fitme.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.setLightStatusBar
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.ui.BaseActivity
import com.example.fitme.databinding.ActivityAuthBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthActivity : BaseActivity<AuthViewModel, ActivityAuthBinding>() {

    override val viewModel: AuthViewModel by viewModel()

    private var navController: NavController? = null

    private val navListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        when (destination.id) {
            R.id.loginFragment,
            R.id.boardingFragment,
            -> {
                setLightStatusBar(true)
                window.statusBarColor = fetchColor(R.color.white)
            }
            R.id.splashFragment -> {
                setLightStatusBar(false)
                window.statusBarColor = fetchColor(R.color.splash_color)
            }
            else -> {
                setLightStatusBar(true)
                window.statusBarColor = fetchColor(R.color.white)
            }
        }
    }

    private fun initNavigationMenu() {
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun initViewModel() {
        super.initViewModel()

        viewModel.loading.observe(this) {
            binding.progressBar.visible = it
        }
    }

    override fun onBackPressed() {

        if (navController?.currentDestination?.id == R.id.boardingFragment ||
            navController?.currentDestination?.id == R.id.loginFragment ||
            navController?.currentDestination?.id == R.id.registrationFragment
        ) {
            finish()
        } else if (navController?.navigateUp() == false) {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigationMenu()
    }

    override fun onResume() {
        super.onResume()
        navController?.addOnDestinationChangedListener(navListener)
    }

    override fun onPause() {
        navController?.removeOnDestinationChangedListener(navListener)
        super.onPause()
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityAuthBinding {
        return ActivityAuthBinding.inflate(inflater)
    }
}