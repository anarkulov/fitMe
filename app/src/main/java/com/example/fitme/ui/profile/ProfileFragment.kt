package com.example.fitme.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitme.R
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentProfileBinding
import com.example.fitme.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseNavFragment<HomeViewModel, FragmentProfileBinding>() {
    override val viewModel: HomeViewModel by viewModel()

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initListeners() {
        super.initListeners()
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