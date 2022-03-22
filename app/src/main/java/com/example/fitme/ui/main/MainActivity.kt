package com.example.fitme.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
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
                window.statusBarColor = fetchColor(R.color.primary_bg)
                binding.navView.visible = true
            }
            else -> {
                window.statusBarColor = fetchColor(R.color.white)
                binding.navView.visible = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        initializeTask.addOnSuccessListener(object: OnSuccessListener {
//            override fun onSuccess(response: DataReadResponse) {
//                interpreter = InterpreterApi.create(modelBuffer, object: InterpreterApi.Options().setRuntime(
//                        InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY))
//            }
//        })
//            .addOnFailureListener(object: OnFailureListener {
//                override fun onFailure(ex: Exception) {
//                    Log.e("Interpreter", "Cannot initialize interpreter", ex)
//                }
//            })
//
//        lifecycleScope.launchWhenStarted { // uses coroutine
//            initializeTask.await()
//        }
    }

    override fun initView() {
        super.initView()

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        this.navController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.navView, navController!!, false)
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