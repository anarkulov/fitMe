package com.example.fitme.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.databinding.FragmentSplashBinding
import com.example.fitme.ui.auth.AuthViewModel
import com.example.fitme.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseNavFragment<AuthViewModel, FragmentSplashBinding>() {

    override val viewModel: AuthViewModel by viewModel()

    override fun initView() {
        super.initView()


//        runAfter(if (BuildConfig.DEBUG) 1000 else 1500) {
//            if (viewModel.getRefreshToken() != null){
//                viewModel.getRefreshToken()?.let {
//                    authClient.renewAuth(it)
//                        .addParameter("scope", "openid profile offline_access")
//                        .start(object: Callback<Credentials, AuthenticationException> {
//
//                            override fun onSuccess(result: Credentials) {
//
//                                viewModel.setAccessToken(result.accessToken)
//                                result.refreshToken?.let { viewModel.setRefreshToken(it) }
//
//                                Log.d(result.accessToken, "accessToken")
//                                Log.d(result.refreshToken, "refreshToken")
//                                Log.d(result.idToken, "idToken")
//                                showLoading(false)

                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
//                            }
//                            override fun onFailure(error: AuthenticationException) {
//                                showLoading(false)
//                            }
//                        })
//                }
//            }else{
//                navigate(SplashFragmentDirections.actionSplashFragmentToBoardingFragment())
//            }
//        }
    }


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentSplashBinding {
        return FragmentSplashBinding.bind(view)
    }
}