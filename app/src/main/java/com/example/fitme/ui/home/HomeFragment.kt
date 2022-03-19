package com.example.fitme.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.R
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.Workout
import com.example.fitme.data.local.Constants.Home.PERIOD_DAY
import com.example.fitme.data.local.Constants.Home.PERIOD_MONTH
import com.example.fitme.data.local.Constants.Home.PERIOD_WEEK
import com.example.fitme.data.local.Constants.Home.TYPE_CALORIE
import com.example.fitme.data.local.Constants.Home.TYPE_COUNTERS
import com.example.fitme.data.local.Constants.Home.TYPE_SECONDS
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.User
import com.example.fitme.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.math.roundToInt


class HomeFragment : BaseNavFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModel()

    private val activityList = ArrayList<Activity>()
    private val myTag = "HomeFragment"
    private val activitiesAdapter: ActivityListRecycler = ActivityListRecycler(activityList, this::onActivityClick)
    private var statisticDataList: MutableList<Pair<String, Float>> = mutableListOf()
    private var statisticActivityList: ArrayList<Activity> = ArrayList()


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
                        Log.d("getProfile: $it", myTag)
                        setData(it)
                    }
                }
            }
        }

        viewModel.getActivityList().observe(this) { response ->
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
                        Log.d("getActivityList: $it", myTag)
                        sortDataList(it)
                    }
                }
            }
        }

        getStatisticActivityList(PERIOD_WEEK)
    }

    private fun getStatisticActivityList(typeWeek: Int) {
        viewModel.getAllActivityCountersBy(typeWeek).observe(this) { response ->
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
                        statisticActivityList.clear()
                        statisticActivityList.addAll(it)
                        setTypeStatistic(typeWeek)
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()

        viewModel.getUserProfile()
        initSpinner()
        setStatisticData()
        initActivityList()
    }

    private fun initSpinner() {
       ArrayAdapter.createFromResource(requireContext(), R.array.categories_array, R.layout.item_spinner).also { adapter ->
           adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
           binding.btnFilterSpinner.adapter = adapter
       }
    }

    private fun setData(user: User) {
        val fullName = "${user.firstName} ${user.lastName}"
        binding.tvName.text = fullName

        if (user.age == null) {
            binding.tvAge.text = "0"
        } else {
            binding.tvAge.text = user.age.toString()
        }

        if (user.height == null) {
            binding.tvHeight.text = "0"
        } else {
            binding.tvHeight.text = user.height.toString()
        }

        if (user.weight == null) {
            binding.tvWeight.text = "0"
        } else {
            binding.tvWeight.text = user.weight.toString()
        }

    }

    private fun setStatisticData() {
        binding.lineChart.animation.duration = 1000
        val labelsFormatter: (Float) -> String = { it.roundToInt().toString() }
        binding.lineChart.labelsFormatter = labelsFormatter
    }

    private fun setTypeStatistic(period: Int = PERIOD_WEEK, type: Int = TYPE_COUNTERS) {
        when (period) {
            PERIOD_MONTH -> {
                calculateStatisticDataForMonth(type)
            }
            PERIOD_WEEK -> {
                calculateStatisticDataForWeek(type)
            }
            PERIOD_DAY -> {
                calculateStatisticDataForAllTime(type)
            }
        }
    }

    private fun calculateStatisticDataForAllTime(type: Int) {
        statisticDataList = mutableListOf(
            "J" to 0F,
            "F" to 0F,
            "M" to 0F,
            "A" to 0F,
            "M" to 0F,
            "M" to 0F,
            "M" to 0F
        )
        binding.lineChart.animate(statisticDataList)
    }

    private fun calculateStatisticDataForMonth(type: Int) {
        statisticDataList = mutableListOf(
            "J" to 0F,
            "F" to 0F,
            "M" to 0F,
            "A" to 0F,
            "M" to 0F,
            "J" to 0F,
            "J" to 0F,
            "A" to 0F,
            "S" to 0F,
            "O" to 0F,
            "N" to 0F,
            "D" to 0F
        )

        binding.lineChart.animate(statisticDataList)
    }

    private fun calculateStatisticDataForWeek(type: Int) {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)

        val nextDay = Calendar.getInstance()
        nextDay.set(Calendar.HOUR_OF_DAY, 0)
        nextDay.set(Calendar.MINUTE, 0)
        nextDay.set(Calendar.SECOND, 0)
        nextDay.set(Calendar.MILLISECOND, 0)

        val startDay = currentCalendar[Calendar.DAY_OF_YEAR] - currentCalendar[Calendar.DAY_OF_WEEK]

        statisticDataList = mutableListOf(
            "Mon" to 0F,
            "Tue" to 0F,
            "Wed" to 0F,
            "Thu" to 0F,
            "Fri" to 0F,
            "Sat" to 0F,
            "Sun" to 0F,
        )

        val hashMapCur = mutableMapOf<Long, Int>()

        for (day in 2 until 9) {
            val weekDay = startDay + day
            currentCalendar.set(Calendar.DAY_OF_YEAR, weekDay)

            hashMapCur[currentCalendar.timeInMillis] = day
            Log.d("${currentCalendar.timeInMillis} - $day", myTag)
        }

        for (item in statisticActivityList) {
            val itemCalendar = Calendar.getInstance()
            itemCalendar.timeInMillis = item.createdAt
            itemCalendar.set(Calendar.HOUR_OF_DAY, 0)
            itemCalendar.set(Calendar.MINUTE, 0)
            itemCalendar.set(Calendar.SECOND, 0)
            itemCalendar.set(Calendar.MILLISECOND, 0)

            val key = itemCalendar.timeInMillis
            if (hashMapCur.containsKey(key)) {
                val index = hashMapCur[key]?.minus(2)
                index?.let {
                    val data = when (type) {
                        TYPE_COUNTERS -> statisticDataList[index].second + item.counters
                        TYPE_CALORIE -> statisticDataList[index].second + item.calories
                        else -> statisticDataList[index].second + item.seconds
                    }
                    statisticDataList[index] = statisticDataList[index].copy(second = data)
                }
            }
        }

        binding.lineChart.animate(statisticDataList)
    }

    private fun initActivityList() {
        binding.activityRecyclerView.apply {
            this.adapter = activitiesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun sortDataList(statisticDataList: List<Activity>) {
        val sortedActivities = ArrayList<Activity>()

        val pushUps = statisticDataList.filter { it.workout == Workout.PushUp.name }
        val squads = statisticDataList.filter { it.workout == Workout.Squad.name }
        val planks = statisticDataList.filter { it.workout == Workout.Plank.name }

        if (pushUps.isNotEmpty()) {
            val pushUp = Activity(name = "My PushUps", createdAt = System.currentTimeMillis())
            for (item in pushUps) {
                if (item.createdAt < pushUp.createdAt) {
                    pushUp.createdAt = item.createdAt
                }
                pushUp.counters += item.counters
                pushUp.calories += item.calories
                pushUp.seconds += item.seconds
            }
            sortedActivities.add(pushUp)
        }

        if (squads.isNotEmpty()) {
            val squad = Activity(name = "My Squads", createdAt = System.currentTimeMillis())
            for (item in squads) {
                if (item.createdAt < squad.createdAt) {
                    squad.createdAt = item.createdAt
                }
                squad.counters += item.counters
                squad.calories += item.calories
                squad.seconds += item.seconds
            }
            sortedActivities.add(squad)
        }

        if (planks.isNotEmpty()) {
            val plank = Activity(name = "My Planks")
            for (item in pushUps) {
                plank.counters += item.counters
                plank.calories += item.calories
                plank.seconds += item.seconds
            }
            sortedActivities.add(plank)
        }

        activitiesAdapter.updateItems(sortedActivities)
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnMonth.setOnClickListener {
            setTypeStatistic(PERIOD_MONTH)
        }

        binding.btnWeek.setOnClickListener {
            setTypeStatistic(PERIOD_WEEK)
        }

        binding.btnDay.setOnClickListener {
            setTypeStatistic(PERIOD_DAY)
        }

        binding.btnEdit.setOnClickListener {
            showToast("Edit is not implemented yet")
        }

        binding.btnFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> setTypeStatistic(PERIOD_WEEK, TYPE_COUNTERS)
                    1 -> setTypeStatistic(PERIOD_WEEK, TYPE_CALORIE)
                    else -> setTypeStatistic(PERIOD_WEEK, TYPE_SECONDS)
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
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
