package com.example.fitme.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentProfileBinding
import com.example.fitme.ui.auth.AuthActivity
import com.example.fitme.ui.home.HomeViewModel
import com.google.firebase.auth.FirebaseUser
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseNavFragment<HomeViewModel, FragmentProfileBinding>() {

    override val viewModel: HomeViewModel by viewModel()
    private lateinit var firebaseUser: FirebaseUser

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()

        viewModel.getUser().observe(this) {
            if (it != null) {
                firebaseUser = it
            }
        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.logout.setOnClickListener {
            viewModel.logOut().observe(this) {
                requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentProfileBinding {
        return FragmentProfileBinding.bind(view)
    }

}