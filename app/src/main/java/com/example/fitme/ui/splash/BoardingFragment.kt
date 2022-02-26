package com.example.fitme.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentBoardingBinding
import com.example.fitme.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BoardingFragment : BaseNavFragment<AuthViewModel, FragmentBoardingBinding>() {

    override val viewModel: AuthViewModel by sharedViewModel()

    override fun initView() {
        super.initView()
    }

    override fun initListeners() {
        super.initListeners()

        binding.getStartedBtn.setOnClickListener {
            navigate(BoardingFragmentDirections.actionBoardingFragmentToLoginFragment())
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentBoardingBinding {
        return FragmentBoardingBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentBoardingBinding {
        return FragmentBoardingBinding.bind(view)
    }
}