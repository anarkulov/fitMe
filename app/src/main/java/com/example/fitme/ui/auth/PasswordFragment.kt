package com.example.fitme.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.fitme.R
import com.example.fitme.core.extentions.hideError
import com.example.fitme.core.extentions.showError
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.core.ui.widgets.MainToolbar
import com.example.fitme.databinding.FragmentPasswordBinding
import com.example.fitme.utils.Utils
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PasswordFragment : BaseNavFragment<AuthViewModel, FragmentPasswordBinding>() {

    override val viewModel: AuthViewModel by sharedViewModel()

    override fun initViewModel() {
        super.initViewModel()
//
//        viewModel.forgotPassword.observe(this, {
//            showLoading(it.status == Status.LOADING)
//            if (it.data == true) {
//                findNavController().popBackStack()
//            } else if (it.data == null) {
//                showToast(it.message)
//            }
//        })
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnSend.setOnClickListener {
            submit()
        }
    }

    private fun submit() {

        val email = binding.etEmail.text.toString()

        if (email.isBlank()) {
            binding.tilEmail.showError(getString(R.string.empty), true)
            return
        } else if (!Utils.isEmailValid(email)) {
            binding.tilEmail.showError(getString(R.string.email_invalid), true)
            return
        } else {
            binding.tilEmail.hideError()
        }

//        viewModel.forgotPassword(email)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentPasswordBinding {
        val v = FragmentPasswordBinding.inflate(inflater, container, false)
        v.toolbar.bind(
            leftButton = MainToolbar.ActionInfo(
                onClick = {
                    requireActivity().onBackPressed()
                }
            )
        )
        return v
    }

    override fun bindViewBinding(view: View): FragmentPasswordBinding {
        return FragmentPasswordBinding.bind(view)
    }

}