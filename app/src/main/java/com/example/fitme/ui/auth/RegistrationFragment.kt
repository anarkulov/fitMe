package com.example.fitme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.fitme.R
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.databinding.FragmentRegistrationBinding
import com.example.fitme.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegistrationFragment : BaseFragment<AuthViewModel, FragmentRegistrationBinding>() {

    override val viewModel: AuthViewModel by sharedViewModel()
    private var firstName = ""
    private var lastName = ""
    private var email = ""
    private var password = ""

    private val myTag = "RegistrationFragment"

    override fun initViewModel() {
        super.initViewModel()

        viewModel.createAccount.observe(this) {
            Log.d("createAccount (observer): data:${it.data}; status:${it.status}; message:${it.message};", myTag)

            if (it.data != null) {
                viewModel.createUserProfile(it.data, firstName, lastName, email, "")
            } else {
                showSnackbar(it.message)
                viewModel.loading.postValue(false)
            }
        }

        viewModel.createUserProfile.observe(viewLifecycleOwner) {
            Log.d("createUserProfile (observer): data:${it.data}; status:${it.status}; message:${it.message};", myTag)

            if (it.status == Status.SUCCESS || it.status == Status.ERROR) {
                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun initView() {
        super.initView()

//        in case to profile update
//        if (!navArgs.email.isEmpty()) {
//            viewBinding.etEmail.setText(navArgs.email)
//            viewBinding.etEmail.isFocusable = false
//            viewBinding.etEmail.isClickable = false
//        }
//        if (!navArgs.password.isEmpty()) {
//            viewBinding.etPassword.setText(navArgs.password)
//        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(RegistrationFragmentDirections.actionRegistrationFragmentToLoginFragment())
        }
    }

    private fun signUp() {
        firstName = binding.etFirstName.text.toString().trim()
        lastName = binding.etLastName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()

        val errorMessage = getString(R.string.empty)
        if (firstName.isEmpty()) {
//            binding.firstNameError.text = ""
            showSnackbar(errorMessage)
            return
        }

        if (lastName.isEmpty()) {
//            binding.firstNameError.text = ""
            showSnackbar(errorMessage)
            return
        }

        if (email.isEmpty()) {
//            binding.firstNameError.text = ""
            showSnackbar(errorMessage)
            return
        }

        if (password.isEmpty()) {
//            binding.firstNameError.text = ""
            if (password.length < 6) {
                showSnackbar(getString(R.string.password_length))
            } else {
                showSnackbar(errorMessage)
            }
            return
        }

        viewModel.loading.postValue(true)
        viewModel.createAccount(email, password)
    }

    private fun showSnackbar(message: String?) {
        activity?.showSnackBar(message)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentRegistrationBinding {
        return FragmentRegistrationBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentRegistrationBinding {
        return FragmentRegistrationBinding.bind(view)
    }
}