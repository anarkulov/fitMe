package com.example.fitme.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitme.R
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.data.models.Activity
import com.example.fitme.databinding.FragmentActivityBinding
import com.example.fitme.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FragmentActivity: BaseFragment<HomeViewModel, FragmentActivityBinding>() {

    override val viewModel: HomeViewModel by sharedViewModel()

    private val activitiesAdapter: ActivitiesAdapter = ActivitiesAdapter(ArrayList())

    private lateinit var activity: Activity
    private val myTag = "FragmentActivity"

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()
        initRecyclerView()
        setActivityList()
    }

    private fun setActivityList() {
        val activities = viewModel.activityList.filter { it.workout == activity.workout }
        activitiesAdapter.updateItems(activities)
        binding.progressBar.visible = false
    }

    private fun initRecyclerView() {
        binding.recyclerViewProduct.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            val verticalDecor = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            val horizontalDecor = DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_border)?.let {
                verticalDecor.setDrawable(it)
                horizontalDecor.setDrawable(it)
            }
            addItemDecoration(verticalDecor)
            addItemDecoration(horizontalDecor)
            adapter = activitiesAdapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity = arguments?.getSerializable(ACTIVITY_KEY) as Activity
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentActivityBinding {
        return FragmentActivityBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentActivityBinding {
        return FragmentActivityBinding.bind(view)
    }

    companion object {
        fun newInstance(activity: Activity): Fragment {
            val fragment = FragmentActivity()
            fragment.arguments = Bundle().apply {
                putSerializable(ACTIVITY_KEY, activity)
            }
            return fragment
        }

        private const val ACTIVITY_KEY = "ACTIVITY_KEY"
    }
}