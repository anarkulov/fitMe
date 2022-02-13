package com.example.fitme.ui.splash.boarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentBoardingBinding
import com.example.fitme.ui.auth.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BoardingFragment : BaseNavFragment<AuthViewModel, FragmentBoardingBinding>() {

    override val viewModel: AuthViewModel by viewModel()

    override fun initView() {
        super.initView()
        requireActivity().window.statusBarColor = fetchColor(R.color.splash_color)
    }

    override fun initListeners() {
        super.initListeners()

        binding.getStartedBtn.setOnClickListener {
//            navigate(BoardingFragmentDirections.actionBoardingFragmentToLoginFragment())
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