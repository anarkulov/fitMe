//package com.example.fitme.ui.auth
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.example.fitme.R
//import com.example.fitme.core.extentions.hideError
//import com.example.fitme.core.extentions.showError
//import com.example.fitme.core.ui.BaseNavFragment
//import com.example.fitme.databinding.FragmentSignUpBinding
//import com.example.fitme.ui.main.MainActivity
//import com.example.fitme.utils.Utils
//import com.google.firebase.auth.FirebaseUser
//import org.koin.androidx.viewmodel.ext.android.sharedViewModel
//
//class SignUpFragment : BaseNavFragment<AuthViewModel, FragmentSignUpBinding>() {
//
//    override val viewModel: AuthViewModel by sharedViewModel()
//    private var firstName = ""
//    private var lastName = ""
//    private var email = ""
//    private var phone = ""
//    private var password = ""
//
//    private var firebaseUser: FirebaseUser? = null
//
//    override fun initView() {
//        super.initView()
//    }
//
//
//
//    override fun initViewModel() {
//        super.initViewModel()
//
//        viewModel.getCurrentFirebaseUser().observe(this) {
//            if (firebaseUser == null) {
//                requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
//                requireActivity().finish()
//            }
//        }
//    }
//
//    override fun initListeners() {
//        super.initListeners()
//
//        binding.btnNext.setOnClickListener {
//            submit()
//        }
//    }
//
//    private fun submit() {
//
////        firstName = binding.etFirstname.text.toString()
////        lastName = binding.etLastname.text.toString()
//        email = binding.etEmail.text.toString()
//        password = binding.etPassword.text.toString()
////        phone = binding.etPhone.text.toString()
//
////        if (firstName.isBlank()) {
////            binding.tilFirstname.showError(getString(R.string.first_name_empty), true)
////            return
////        } else {
////            binding.tilFirstname.hideError()
////        }
////
////        if (lastName.isBlank()) {
////            binding.tilLastname.showError(getString(R.string.last_name_empty), true)
////            return
////        } else {
////            binding.tilLastname.hideError()
////        }
//
//        if (email.isBlank()) {
//            binding.tilEmail.showError(getString(R.string.empty), true)
//
//            return
//        } else if (!Utils.isEmailValid(email)) {
//            binding.tilEmail.showError(getString(R.string.email_invalid), true)
//            return
//        } else {
//            binding.tilEmail.hideError()
//        }
//
////        if (phone.isBlank()) {
////            binding.tilPhone.showError(getString(R.string.phone_empty), true)
////            return
////        } else {
////            binding.tilPhone.hideError()
////        }
////
//        when {
//            password.isBlank() -> {
//                binding.tilPassword.showError(getString(R.string.empty), true)
//                return
//            }
//            password.length < 6 -> {
//                binding.tilPassword.showError(getString(R.string.password_length), true)
//            }
//            else -> {
//                binding.tilPassword.hideError()
//            }
//        }
//
////        if (!binding.checkbox.isChecked) {
////            AnimationHelper.vibrate(requireContext(), binding.layoutTerms)
////            binding.tvCheckboxError.visible = true
////            return
////        } else {
////            binding.tvCheckboxError.visible = false
////        }
////
//        viewModel.register(email, password).observe(viewLifecycleOwner) {
//
//        }
//
////        if (navArgs.uid.isNullOrEmpty()) {
////        } else {
////            navArgs.uid.let {
////                if (it != null) {
////                    viewModel.createUserProfile(it, firstName, lastName, email, phone)
////                }
////            }
////        }
//
//        /* navigate(SignUpFragmentDirections.actionSignUpFragmentToOtpFragment())*/
//    }
//
//    override fun inflateViewBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): FragmentSignUpBinding {
//        return FragmentSignUpBinding.inflate(inflater, container, false)
//    }
//
//    override fun bindViewBinding(view: View): FragmentSignUpBinding {
//        return FragmentSignUpBinding.bind(view)
//    }
//
//}