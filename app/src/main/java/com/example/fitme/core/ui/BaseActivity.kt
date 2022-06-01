package com.example.fitme.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.fitme.R
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.utils.Utils

abstract class BaseActivity<out VM : BaseViewModel, VB : ViewBinding>: AppCompatActivity() {

    protected abstract val viewModel: VM
    protected lateinit var binding: VB

    protected abstract fun inflateViewBinding(inflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = inflateViewBinding(LayoutInflater.from(this))

        val lang = AppPrefs(this).lang
        if (lang != null) {
            Utils.setLocale(resources, lang)
        } else {
            Utils.setLocale(resources, getString(R.string.en_locale))
        }

        setContentView(binding.root)

        initViewModel()
        initView()
        initListeners()
    }

    open fun initViewModel() {
//        viewModel.loading.observe(this, {
//            findViewById<ProgressBar>(R.id.progress_bar).visible = it
//        })
    }
    open fun initView() {}
    open fun initListeners() {}

    fun showLoading(value: Boolean) {
        viewModel.loading.postValue(value)
    }
}
