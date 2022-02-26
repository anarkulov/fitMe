package com.example.fitme.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitme.R
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentLoginBinding
import com.example.fitme.ui.main.MainActivity
import com.example.fitme.utils.Utils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : BaseNavFragment<AuthViewModel, FragmentLoginBinding>() {

    override val viewModel: AuthViewModel by sharedViewModel()

    private val myTag = "LoginFragment"

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnSignUp.setOnClickListener {
            navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
        }

        binding.btnForgotPassword.setOnClickListener {
            navigate(LoginFragmentDirections.actionLoginFragmentToPasswordFragment())
        }

        binding.btnLogin.setOnClickListener {
            loginByEmail()
        }

    }

    private fun loginByEmail() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()



        if (email.isBlank()) {
            showSnackBar(getString(R.string.empty))
            return
        } else if (!Utils.isEmailValid(email)) {
            showSnackBar("Email is not valid")
            return
        }

        when {
            password.isBlank() -> {
                showSnackBar(getString(R.string.empty))
                return
            }
            password.length < 6 -> {
                showSnackBar(getString(R.string.password_length))
            }
        }

        viewModel.login(email, password).observe(viewLifecycleOwner) {
            when(it.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    if (it != null) {
                        viewModel.loading.postValue(false)
                        handleSuccessLogin()
                    }
                }
            }
        }
    }

    private fun showSnackBar(s: String) {
        activity?.showSnackBar(s)
    }

    private fun hasName(): Boolean {
        var isTrue = false
//        val myUserId = FirebaseAuth.getInstance().uid
//        if (myUserId != null) {
//            try {
//                FirebaseFirestore
//                    .getInstance()
//                    .collection(USERS)
//                    .document(myUserId)
//                    .get()
//                    .addOnSuccessListener { snapshot ->
//                        val user: User? = snapshot.toObject(User::class.java)
//                        if (user != null) {
//                            user.id = snapshot.id
//                            if (!TextUtils.isEmpty(user.id)) {
//                                isTrue = true
//                            }
//                        }
//                    }
//            } catch (npe: java.lang.NullPointerException) {
//                npe.printStackTrace()
//            }
//        }

        return isTrue
    }

    private fun handleSuccessLogin() {
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentLoginBinding {
        return FragmentLoginBinding.bind(view)
    }
}