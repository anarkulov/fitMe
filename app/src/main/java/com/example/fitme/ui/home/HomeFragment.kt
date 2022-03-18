package com.example.fitme.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.data.local.Constants.Home.TYPE_DAY
import com.example.fitme.data.local.Constants.Home.TYPE_MONTH
import com.example.fitme.data.local.Constants.Home.TYPE_WEEK
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.User
import com.example.fitme.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class HomeFragment : BaseNavFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModel()

    private val activityList = ArrayList<Activity>()
    private val myTag = "HomeFragment"

    override fun initViewModel() {
        super.initViewModel()

        viewModel.getProfile.observe(this) { response ->
            when(response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    response.data?.let {
                        setData(it)
                    }
                }
            }
        }
    }

    private fun setData(user: User) {
        val fullName = "${user.firstName} ${user.lastName}"
        binding.tvName.text = fullName

        if (user.age == null) {
            binding.tvAge.text = "0"
        } else {
            binding.tvAge.text = user.age
        }

        if (user.height == null) {
            binding.tvHeight.text = "0"
        } else {
            binding.tvHeight.text = user.height
        }

        if (user.weight == null) {
            binding.tvWeight.text = "0"
        } else {
            binding.tvWeight.text = user.height
        }

    }

    override fun initView() {
        super.initView()

        viewModel.getUserProfile()
        setStatisticData()
        initActivityList()
    }

    var dataList: List<Pair<String, Float>>? = null

    private fun setStatisticData() {
        binding.lineChart.animation.duration = 1000
        val labelsFormatter: (Float) -> String = { it.roundToInt().toString() }
        binding.lineChart.labelsFormatter = labelsFormatter

        setTypeStatistic()
    }

    private fun setTypeStatistic(type: Int = TYPE_WEEK) {

        when (type) {
            TYPE_MONTH -> {
                dataList = listOf(
                    "1" to 4F,
                    "2" to 10F,
                    "3" to 2F,
                    "4" to 3F,
                    "5" to 5F,
                    "6" to 4F,
                    "7" to 6F
                )
            }
            TYPE_WEEK -> {
                dataList = listOf(
                    "Mon" to 1F,
                    "Tue" to 4F,
                    "Wed" to 5F,
                    "Thu" to 2F,
                    "Fri" to 7F,
                    "Sat" to 5F,
                    "Sun" to 8F
                )
            }
            TYPE_DAY -> {
                dataList = listOf(
                    "04:00" to 10F,
                    "08:00" to 20F,
                    "12:00" to 2F,
                    "16:00" to 3F,
                    "20:00" to 5F,
                    "00:00" to 8F
                )
            }
        }

        dataList?.let { binding.lineChart.animate(it) }
    }

    private fun initActivityList() {
        activityList.clear()
        for (i in 0 until 2) {
            activityList.add(Activity("${i + i % (i + 1) * 2}",
                "Activity ${i.plus(1)}",
                "${284*i+1} kCal on this month"
            ))
        }

        val adapter = ActivityListRecycler(activityList, this::onActivityClick)

        binding.activityRecyclerView.apply {
            this.adapter = adapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnMonth.setOnClickListener {
            setTypeStatistic(TYPE_MONTH)
        }

        binding.btnWeek.setOnClickListener {
            setTypeStatistic(TYPE_WEEK)
        }

        binding.btnMonth.setOnClickListener {
            setTypeStatistic(TYPE_DAY)
        }
    }

    private fun onActivityClick(activity: Activity) {

    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentHomeBinding {
        return FragmentHomeBinding.bind(view)
    }

}
