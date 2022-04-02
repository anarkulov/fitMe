package com.example.fitme.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.data.models.Activity
import com.example.fitme.databinding.FragmentMyActivityBinding
import com.example.fitme.ui.home.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MyActivitiesFragment: BaseFragment<HomeViewModel, FragmentMyActivityBinding>() {

    override val viewModel: HomeViewModel by sharedViewModel()
    private val navArgs: MyActivitiesFragmentArgs by navArgs()
    private val myTag = "MyActivitiesFragment"

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()

        initActivityPager()
    }

    private fun initActivityPager() {
        val activityList = viewModel.sortedActivityList
//        Log.d("list: $activityList", myTag)
        val pagerAdapter = ActivityViewPagerAdapter(activityList,this)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.isSaveEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = activityList[position].name
        }.attach()

        for (i in 0 until binding.tabLayout.tabCount) {
            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            navArgs.activityId?.let {
                if (it == activityList[i].id) {
                    binding.tabLayout.getTabAt(i)?.select()
                    tab.requestLayout()
                    return
                }
            }
        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentMyActivityBinding {
        return FragmentMyActivityBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentMyActivityBinding {
        return FragmentMyActivityBinding.bind(view)
    }


    inner class ActivityViewPagerAdapter(private val list: ArrayList<Activity>, fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = list.size

        override fun createFragment(position: Int): Fragment {
            return FragmentActivity.newInstance(list[position])
        }
    }

}