package com.example.fitme.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VM: ViewModel, VB: ViewBinding> : Fragment() {

    protected abstract val viewModel: VM
    protected lateinit var binding: VB
    private var _view: View? = null //cached view

    protected abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): VB
    protected abstract fun bindViewBinding(view: View): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        if(_view == null) {
            binding = inflateViewBinding(inflater, container, savedInstanceState)
            _view = binding.root
        } else {
            binding = bindViewBinding(_view!!)
        }
        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initView()
        initListeners()
    }

    open fun initViewModel() {}
    open fun initView() {}
    open fun initListeners() {}
}
