package com.example.fitme.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.ui.widgets.MainToolbar
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.FragmentAlarmDetailsBinding
import com.example.fitme.ui.alarm.pose.PoseBottomSheetFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AlarmDetails : BaseFragment<AlarmViewModel, FragmentAlarmDetailsBinding>() {

    private val myTag = "AlarmDetails"

    override val viewModel: AlarmViewModel by viewModel()
    private val navArgs: AlarmDetailsArgs by navArgs()

    private var timeHour = -1
    private var minute = -1
    private val days = TreeMap<Int, Boolean>()

    override fun initViewModel() {
        super.initViewModel()

        viewModel.isChanged.observe(this) {
            binding.btnSave.isEnabled = it
        }

        viewModel.loading.observe(this) {
            binding.loading.visible = it
        }
    }

    override fun initView() {
        super.initView()
        binding.time.setIs24HourView(true)
        binding.btnSave.isEnabled = false
        setData()
    }

    private fun setData() {

        val data = navArgs.alarm

        if (data == null) {
            val calendar = Calendar.getInstance()
            binding.time.hour = calendar.get(Calendar.HOUR)
            binding.time.minute = calendar.get(Calendar.MINUTE)
            return
        }

        binding.etAlarmName.setText(data.title)
        val splitTime = data.time.split(":")
        timeHour = splitTime[0].toInt()
        minute = splitTime[1].toInt()

        binding.time.hour = timeHour
        binding.time.minute = minute
        if (data.challenge.isNotEmpty() || data.challenge != "none") {
            binding.tvPoseName.text = data.challenge
        }

        for ((index, day) in data.days.withIndex()) {
            days[index] = day
            if (day) {
                when (index) {
                    0 -> {
                        binding.cvMonday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvMonday.setTextColor(fetchColor(R.color.white))
                        binding.cvMonday.isSelected = true
                    }
                    1 -> {
                        binding.cvTuesday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvTuesday.setTextColor(fetchColor(R.color.white))
                        binding.cvTuesday.isSelected = true
                    }
                    2 -> {
                        binding.cvWednesday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvWednesday.setTextColor(fetchColor(R.color.white))
                        binding.cvWednesday.isSelected = true

                    }
                    3 -> {
                        binding.cvThursday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvThursday.setTextColor(fetchColor(R.color.white))
                        binding.cvThursday.isSelected = true

                    }
                    4 -> {
                        binding.cvFriday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvFriday.setTextColor(fetchColor(R.color.white))
                        binding.cvFriday.isSelected = true

                    }
                    5 -> {
                        binding.cvSaturday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvSaturday.setTextColor(fetchColor(R.color.white))
                        binding.cvSaturday.isSelected = true

                    }
                    6 -> {
                        binding.cvSunday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvSunday.setTextColor(fetchColor(R.color.white))
                        binding.cvSunday.isSelected = true
                    }
                }
            }
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnSelectPose.setOnClickListener {
            val bottomSheet = PoseBottomSheetFragment(this::poseSelect)
            bottomSheet.show(childFragmentManager, "tag")
        }

        binding.btnSave.setOnClickListener {
            saveData()
        }

        binding.etAlarmName.doAfterTextChanged {
            viewModel.isChanged.postValue(true)
        }

        binding.time.setOnTimeChangedListener { _, _, _ ->
            viewModel.isChanged.postValue(true)
        }

        binding.cvMonday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[0] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvMonday)
        }
        binding.cvTuesday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[1] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvTuesday)
        }
        binding.cvWednesday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[2] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvWednesday)
        }
        binding.cvThursday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[3] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvThursday)
        }
        binding.cvFriday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[4] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvFriday)
        }
        binding.cvSaturday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[5] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvSaturday)
        }
        binding.cvSunday.setOnClickListener {
            it as CardView
            it.isSelected = !it.isSelected
            days[6] = it.isSelected
            setDayColor(it.isSelected, it, binding.tvSunday)
        }
    }

    private fun poseSelect(pose: String) {
        binding.tvPoseName.text = pose.trim()
        viewModel.isChanged.postValue(true)
    }

    private fun saveData() {
        timeHour = binding.time.hour
        minute = binding.time.minute

        if (timeHour == -1 || minute == -1) {
            showToast("Set time")
            return
        }

        var alarmTitle = binding.etAlarmName.text.toString()
        if (alarmTitle.isEmpty()) {
            alarmTitle = "Alarm ${Date().time.mod(12)}"
        }

        val repeatDays = ArrayList<Boolean>()
        var isRepeatable = false
        for (i in 0 until 7) {
            if (days[i] == true) {
                isRepeatable = true
                repeatDays.add(true)
            } else {
                repeatDays.add(false)
            }
        }

        val challenge: String = binding.tvPoseName.text.ifEmpty {
            "none"
        } as String

        val alarm =
            Alarm(System.currentTimeMillis().toString(),
                time = "${getString(R.string.hour_format, timeHour)}:${getString(R.string.hour_format, minute)}",
                docId = if (navArgs.alarm != null) navArgs.alarm!!.docId else "",
                title = alarmTitle,
                days = repeatDays,
                challenge = challenge,
                isTurnedOn = false,
                isRepeatable = isRepeatable,
                timeInMs = 0
            )

        Log.d("alarm : $alarm", myTag)

        if (navArgs.alarm == null) {
            saveAlarm(alarm)
        } else {
            updateData(alarm)
        }
    }

    private fun saveAlarm(alarm: Alarm) {
        viewModel.saveAlarmData(alarm).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    requireActivity().showSnackBar("Alarm is added")
                    findNavController().popBackStack()
                }
            }
        }
    }


    private fun onDeleteClick() {
        if (navArgs.alarm != null) {
            viewModel.deleteAlarmData(navArgs.alarm?.docId).observe(this) { response ->
                when (response.status) {
                    Status.LOADING -> {
                        viewModel.loading.postValue(true)
                    }
                    Status.ERROR -> {
                        viewModel.loading.postValue(false)
                    }
                    Status.SUCCESS -> {
                        viewModel.loading.postValue(false)
                        requireActivity().showSnackBar("Alarm is deleted")
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }


    private fun updateData(alarm: Alarm) {
        viewModel.updateAlarm(alarm).observe(this) { response ->
            when (response.status) {
                Status.LOADING -> {
                    viewModel.loading.postValue(true)
                }
                Status.ERROR -> {
                    viewModel.loading.postValue(false)
                }
                Status.SUCCESS -> {
                    viewModel.loading.postValue(false)
                    requireActivity().showSnackBar("Alarm is updated")
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setDayColor(value: Boolean, view: CardView, tView: AppCompatTextView) {
        if (value) {
            view.setCardBackgroundColor(fetchColor(R.color.blue))
            tView.setTextColor(fetchColor(R.color.white))
        } else {
            view.setCardBackgroundColor(fetchColor(R.color.white))
            tView.setTextColor(fetchColor(R.color.black))
        }
        viewModel.isChanged.postValue(true)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentAlarmDetailsBinding {
        return FragmentAlarmDetailsBinding.inflate(inflater, container, false).apply {
            toolbar.leftIcon.setOnClickListener {
                findNavController().popBackStack()
            }
            if (navArgs.alarm != null) {
                toolbar.bind(
                    rightButton = MainToolbar.ActionInfo(
                        R.drawable.ic_delete,
                        iconTint = R.color.blue,
                        onClick = { onDeleteClick() })
                )
            }
        }
    }

    override fun bindViewBinding(view: View): FragmentAlarmDetailsBinding {
        return FragmentAlarmDetailsBinding.bind(view)
    }

}
