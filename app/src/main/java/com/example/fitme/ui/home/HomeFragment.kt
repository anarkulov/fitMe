package com.example.fitme.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.formatCount
import com.example.fitme.core.extentions.loadUrl
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.enums.Workout
import com.example.fitme.data.local.AppPrefs
import com.example.fitme.data.local.Constants.Home.PERIOD_DAY
import com.example.fitme.data.local.Constants.Home.PERIOD_MONTH
import com.example.fitme.data.local.Constants.Home.PERIOD_WEEK
import com.example.fitme.data.local.Constants.Home.TYPE_CALORIE
import com.example.fitme.data.local.Constants.Home.TYPE_COUNTERS
import com.example.fitme.data.local.Constants.Home.TYPE_SECONDS
import com.example.fitme.data.models.Activity
import com.example.fitme.data.models.User
import com.example.fitme.databinding.FragmentHomeBinding
import com.example.fitme.ui.auth.AuthActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : BaseNavFragment<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by sharedViewModel()
    private val appPrefs: AppPrefs by inject()

    private val activityList = ArrayList<Activity>()
    private val myTag = "HomeFragment"
    private val activitiesAdapter: ActivityListRecycler =
        ActivityListRecycler(activityList, this::onActivityClick)
    private var statisticDataList: MutableList<Pair<String, Float>> = mutableListOf()
    private var statisticActivityList: ArrayList<Activity> = ArrayList()

    private var selectedType = TYPE_COUNTERS
    private var selectedPeriod = PERIOD_MONTH
    private var rank = 0

    override fun initViewModel() {
        super.initViewModel()

        viewModel.getProfile.observe(this) { response ->
            when (response.status) {
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
                        viewModel.setProfile(it)
                        setData(it)
                    }
                }
            }
        }

        viewModel.getActivityList().observe(this) { response ->
            when (response.status) {
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
                        viewModel.activityList.clear()
                        viewModel.activityList.addAll(it)
                        sortDataList(it)
                    }
                }
            }
        }

        getStatisticActivityList(selectedPeriod)
    }

    private fun getStatisticActivityList(period: Int) {
        selectedPeriod = period
        viewModel.getAllActivityCountersBy(period).observe(this) { response ->
            when (response.status) {
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
                        setTypeStatistic(period, selectedType)
                    }
                }
            }
        }
    }

    override fun initView() {
        super.initView()

        initTab()
        viewModel.getUserProfile()
        initSpinner()
        setStatisticData()
        initActivityList()
    }

    private fun initTab() {
        when (selectedPeriod) {
            PERIOD_WEEK -> {
                binding.btnWeek.isSelected = true
            }
            PERIOD_MONTH -> {
                binding.btnMonth.isSelected = true
            }
            else -> {
//                binding.btnDay.isSelected = !binding.btnDay.isSelected
            }
        }
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            if (appPrefs.lang == requireContext().getString(R.string.en_locale)) R.array.categories_arra_en else R.array.categories_array_kg,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            binding.btnFilterSpinner.adapter = adapter
        }
    }

    private fun setData(user: User) {
        val fullName = "${user.firstName} ${user.lastName}"
        binding.tvName.text = fullName

        binding.ivAvatar.loadUrl(user.image, R.drawable.ic_person_placeholder)

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

        setRank(rank)
    }

    private fun setStatisticData() {
        binding.lineChart.animation.duration = 1000
        val labelsFormatter: (Float) -> String = { it.roundToInt().formatCount() }
        binding.lineChart.labelsFormatter = labelsFormatter
    }

    private fun setTypeStatistic(period: Int, type: Int) {
        selectedPeriod = period
        selectedType = type
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
            "Today" to 0F
        )
        binding.lineChart.barsColorsList = listOf(Color.WHITE)
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

        val currentCalendar = Calendar.getInstance()
        val today = currentCalendar[Calendar.MONTH]
        currentCalendar.set(Calendar.DAY_OF_YEAR, 1)
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)

        val startDay = 0
        val hashMapCur = mutableMapOf<Long, Int>()
        val colorList = arrayListOf<Int>()
        for (month in 0 until 12) {
            val yearMonth = startDay + month
            currentCalendar.set(Calendar.MONTH, yearMonth)

            hashMapCur[currentCalendar.timeInMillis] = yearMonth

            if (yearMonth == today) {
                colorList.add(fetchColor(R.color.purple))
            } else {
                colorList.add(Color.WHITE)
            }
            Log.d("month: $yearMonth ${currentCalendar.timeInMillis}", myTag)
        }

        rank = 0

        for (item in statisticActivityList) {
            val itemCalendar = Calendar.getInstance()
            itemCalendar.timeInMillis = item.createdAt
            itemCalendar.set(Calendar.DAY_OF_MONTH, 1)
            itemCalendar.set(Calendar.HOUR_OF_DAY, 0)
            itemCalendar.set(Calendar.MINUTE, 0)
            itemCalendar.set(Calendar.SECOND, 0)
            itemCalendar.set(Calendar.MILLISECOND, 0)

            val key = itemCalendar.timeInMillis
            if (hashMapCur.containsKey(key)) {
                val index = hashMapCur[key]
                index?.let {
                    val data = when (type) {
                        TYPE_COUNTERS -> {
                            rank += item.counters
                            statisticDataList[index].second + item.counters
                        }
                        TYPE_CALORIE -> {
                            rank += item.calories
                            statisticDataList[index].second + item.calories
                        }
                        else -> {
                            rank += item.seconds
                            statisticDataList[index].second + item.seconds
                        }
                    }
                    statisticDataList[index] = statisticDataList[index].copy(second = data)
                }
            }
        }

        binding.lineChart.barsColorsList = colorList
        binding.lineChart.animate(statisticDataList)
    }

    private fun setRank(rank: Int) {
        viewModel.getLocalProfile()?.apply {
            this.score = rank
            viewModel.updateUser(this)
        }
    }

    private fun calculateStatisticDataForWeek(type: Int) {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)

        val weekday = currentCalendar[Calendar.DAY_OF_WEEK]
        val monday = Calendar.MONDAY
        val today = if ((weekday - monday) < 0) (7 - (monday - weekday)) else (weekday - monday)
        val startDay = currentCalendar[Calendar.DAY_OF_YEAR] - today

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
        val colorList = arrayListOf<Int>()
        for (day in 0 until 7) {
            val weekDay = startDay + day
            currentCalendar.set(Calendar.DAY_OF_YEAR, weekDay)
            hashMapCur[currentCalendar.timeInMillis] = day

            if (day == today) {
                colorList.add(fetchColor(R.color.purple))
            } else {
                colorList.add(Color.WHITE)
            }
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
                val index = hashMapCur[key]
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

        binding.lineChart.barsColorsList = colorList
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
        val squats = statisticDataList.filter { it.workout == Workout.Squat.name }
        val planks = statisticDataList.filter { it.workout == Workout.Plank.name }

        if (pushUps.isNotEmpty()) {
            val pushUp = Activity(name = "My PushUps", id = System.currentTimeMillis().toString(), createdAt = System.currentTimeMillis(), workout = Workout.PushUp.name)
            for (item in pushUps) {
                if (item.createdAt < pushUp.createdAt) {
                    pushUp.createdAt = item.createdAt
                }
                if (item.imageUrl.isNotEmpty() && pushUp.imageUrl.isEmpty()) {
                    pushUp.imageUrl = item.imageUrl
                }
                pushUp.counters += item.counters
                pushUp.calories += item.calories
                pushUp.seconds += item.seconds
            }
            sortedActivities.add(pushUp)
        }

        if (squats.isNotEmpty()) {
            val squat = Activity(name = "My Squats", id = System.currentTimeMillis().toString(), createdAt = System.currentTimeMillis(), workout = Workout.Squat.name)
            for (item in squats) {
                if (item.createdAt < squat.createdAt) {
                    squat.createdAt = item.createdAt
                }
                if (item.imageUrl.isNotEmpty() && squat.imageUrl.isEmpty()) {
                    squat.imageUrl = item.imageUrl
                }
                squat.counters += item.counters
                squat.calories += item.calories
                squat.seconds += item.seconds
            }
            sortedActivities.add(squat)
        }

        if (planks.isNotEmpty()) {
            val plank = Activity(name = "My Planks", id = System.currentTimeMillis().toString(), createdAt = System.currentTimeMillis(), workout = Workout.Plank.name)
            for (item in pushUps) {
                if (item.createdAt < plank.createdAt) {
                    plank.createdAt = item.createdAt
                }
                if (item.imageUrl.isNotEmpty() && plank.imageUrl.isEmpty()) {
                    plank.imageUrl = item.imageUrl
                }
                plank.counters += item.counters
                plank.calories += item.calories
                plank.seconds += item.seconds
            }
            sortedActivities.add(plank)
        }

        activitiesAdapter.updateItems(sortedActivities)
        viewModel.sortedActivityList.clear()
        viewModel.sortedActivityList.addAll(sortedActivities)
    }

    private fun createDialog() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(getString(R.string.select_language))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.kyrgyz)) { _, _ ->
                viewModel.setLocale(getString(R.string.kg_locale))
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.english)) { _, _ ->
                viewModel.setLocale(getString(R.string.en_locale))
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun initListeners() {
        super.initListeners()

        binding.btnLang.setOnClickListener {
            createDialog()
        }

        binding.btnSignOut.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())

            dialogBuilder.setMessage("Are you sure you want to log out?")
                .setCancelable(true)
                .setPositiveButton(context?.getString(R.string.yes)) { _, _ ->
                    signOut()
                }
                .setNegativeButton(context?.getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }

            val alert = dialogBuilder.create()
            alert.show()
        }

        binding.btnMonth.setOnClickListener {
            binding.btnMonth.isSelected = true
            binding.btnWeek.isSelected = false
//            binding.btnDay.isSelected = false
            getStatisticActivityList(PERIOD_MONTH)
        }

        binding.btnWeek.setOnClickListener {
            binding.btnWeek.isSelected = true
            binding.btnMonth.isSelected = false
//            binding.btnDay.isSelected = false
            getStatisticActivityList(PERIOD_WEEK)
        }

//        binding.btnDay.setOnClickListener {
//            binding.btnDay.isSelected = true
////            binding.btnWeek.isSelected = false
//            binding.btnMonth.isSelected = false
//            setTypeStatistic(PERIOD_DAY, selectedType)
//        }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPeofileEditFragment())
        }

        binding.btnFilterSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (p2) {
                        0 -> setTypeStatistic(selectedPeriod, TYPE_COUNTERS)
                        1 -> setTypeStatistic(selectedPeriod, TYPE_CALORIE)
                        else -> setTypeStatistic(selectedPeriod, TYPE_SECONDS)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
    }

    private fun signOut() {
        viewModel.logOut().observe(this) {
            requireActivity().startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun onActivityClick(activityId: String) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToFragmentMyActivities(activityId))
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
