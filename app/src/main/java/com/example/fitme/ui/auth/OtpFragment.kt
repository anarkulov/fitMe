//package com.example.fitme.ui.auth
//
//import android.os.Bundle
//import android.os.CountDownTimer
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import com.example.fitme.R
//import com.example.fitme.core.extentions.fetchColor
//import com.example.fitme.core.extentions.visible
//import com.example.fitme.core.ui.BaseNavFragment
//import com.example.fitme.databinding.FragmentOtpBinding
//import org.koin.androidx.viewmodel.ext.android.viewModel
//import org.koin.androidx.viewmodel.ext.android.sharedViewModel
//import java.util.*
//
//class OtpFragment : BaseNavFragment<AuthViewModel, FragmentOtpBinding>() {
//
//    override val viewModel: AuthViewModel by sharedViewModel()
//
//    private val timer = object : CountDownTimer(60000, 1000) {
//        override fun onTick(millisUntilFinished: Long) {
//            if (isAdded){
//                binding.tvResend.text = String.format(
//                    Locale.getDefault(),
//                    getString(R.string.resend_in),
//                    millisUntilFinished / 1000
//                )
//            }
//        }
//
//        override fun onFinish() {
//            if (isAdded) {
//                binding.tvResend.text = getString(R.string.resend_in)
//                binding.tvResend.setTextColor(fetchColor(R.color.black))
//                binding.tvResend.setOnClickListener {
//                    resendCode()
//                }
//            }
//        }
//    }
//
//    private fun resendCode() {
////        val phone = viewModel.getPhone().toString()
//        showLoading(true)
//
//    }
//
//    override fun initView() {
//        super.initView()
//    }
//
//    override fun initViewModel() {
//        super.initViewModel()
//    }
//
//    override fun initListeners() {
//        super.initListeners()
//
//        binding.etOtp1.addTextChangedListener(GenericTextWatcher(binding.etOtp1, binding.etOtp2))
//        binding.etOtp2.addTextChangedListener(GenericTextWatcher(binding.etOtp2, binding.etOtp3))
//        binding.etOtp3.addTextChangedListener(GenericTextWatcher(binding.etOtp3, binding.etOtp4))
//        binding.etOtp4.addTextChangedListener(GenericTextWatcher(binding.etOtp4, binding.etOtp5))
//        binding.etOtp5.addTextChangedListener(GenericTextWatcher(binding.etOtp5, binding.etOtp6))
//        binding.etOtp6.addTextChangedListener(GenericTextWatcher(binding.etOtp6, binding.etOtp6))
//
//        binding.etOtp1.setOnKeyListener(GenericKeyEvent(binding.etOtp1, null))
//        binding.etOtp2.setOnKeyListener(GenericKeyEvent(binding.etOtp2, binding.etOtp1))
//        binding.etOtp3.setOnKeyListener(GenericKeyEvent(binding.etOtp3, binding.etOtp2))
//        binding.etOtp4.setOnKeyListener(GenericKeyEvent(binding.etOtp4, binding.etOtp3))
//        binding.etOtp5.setOnKeyListener(GenericKeyEvent(binding.etOtp5, binding.etOtp4))
//        binding.etOtp6.setOnKeyListener(GenericKeyEvent(binding.etOtp6, binding.etOtp5))
//    }
//
//    inner class GenericTextWatcher internal constructor(
//        private val currentView: View,
//        private val nextView: View?
//    ) : TextWatcher {
//        override fun afterTextChanged(editable: Editable) {
//            binding.tvCodeError.visible = false
//            val text = editable.toString()
//            nextView?.let { nextView ->
//                when (currentView.id) {
//                    R.id.et_otp_1 -> if (text.length == 1) nextView.requestFocus()
//                    R.id.et_otp_2 -> if (text.length == 1) nextView.requestFocus()
//                    R.id.et_otp_3 -> if (text.length == 1) nextView.requestFocus()
//                    R.id.et_otp_4 -> if (text.length == 1) nextView.requestFocus()
//                    R.id.et_otp_5 -> if (text.length == 1) nextView.requestFocus()
//                    R.id.et_otp_6 -> if (text.length == 1) submit()
//                }
//            }
//        }
//        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
//        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
//    }
//
//    inner class GenericKeyEvent internal constructor(
//        private val currentView: EditText,
//        private val previousView: EditText?
//    ) : View.OnKeyListener {
//        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
//            binding.tvCodeError.visible = false
//            if (event!!.action == KeyEvent.ACTION_DOWN &&
//                keyCode == KeyEvent.KEYCODE_DEL &&
//                currentView.id != R.id.et_otp_1 &&
//                currentView.text.isEmpty()
//            ) {
//                previousView?.let {
//                    previousView.text = null
//                    previousView.requestFocus()
//                }
//                return true
//            }
//            return false
//        }
//    }
//
//    private fun submit() {
////
////        val code = viewBinding.etCode.text.toString()
////
////        when {
////            code.isBlank() -> {
////                AnimationHelper.vibrate(requireContext(), viewBinding.layoutCode)
////                viewBinding.tvError.text = getString(R.string.otp_empty)
////                viewBinding.tvError.invisible = false
////                return
////            }
////            code.length < 6 -> {
////                AnimationHelper.vibrate(requireContext(), viewBinding.layoutCode)
////                viewBinding.tvError.text = getString(R.string.otp_invalid)
////                viewBinding.tvError.invisible = false
////                return
////            }
////            else -> {
////                viewBinding.tvError.invisible = true
////            }
////        }
////
////        if (navArgs.forgotPassword) {
////            navigate(OtpFragmentDirections.actionOtpFragmentToResetPasswordFragment())
////        }
////        else {
////            requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
////            requireActivity().finish()
////        }
//    }
//
//    override fun inflateViewBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): FragmentOtpBinding {
//        return FragmentOtpBinding.inflate(inflater, container, false)
//    }
//
//    override fun bindViewBinding(view: View): FragmentOtpBinding {
//        return FragmentOtpBinding.bind(view)
//    }
//
//}