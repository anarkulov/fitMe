package com.example.fitme.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fitme.core.extentions.runAfter
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentSplashBinding
import com.example.fitme.ui.auth.AuthViewModel
import com.example.fitme.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : BaseNavFragment<AuthViewModel, FragmentSplashBinding>() {

    override val viewModel: AuthViewModel by sharedViewModel()

    override fun initView() {
        super.initView()

        val currentUser = FirebaseAuth.getInstance().currentUser

        runAfter(1500) {
            if (currentUser != null) {
                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToBoardingFragment())
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentSplashBinding {
        return FragmentSplashBinding.bind(view)
    }
}