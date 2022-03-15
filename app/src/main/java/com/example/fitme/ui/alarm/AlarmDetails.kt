package com.example.fitme.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.showSnackBar
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.FragmentAlarmDetailsBinding
import com.example.fitme.ui.alarm.pose.PoseBottomSheetFragment
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AlarmDetails : BaseFragment<AlarmViewModel, FragmentAlarmDetailsBinding>() {

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
        val splitTime = data.timestamp.split(":")
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
                    }
                    1 -> {
                        binding.cvTuesday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvTuesday.setTextColor(fetchColor(R.color.white))
                    }
                    2 -> {
                        binding.cvWednesday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvWednesday.setTextColor(fetchColor(R.color.white))
                    }
                    3 -> {
                        binding.cvThursday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvThursday.setTextColor(fetchColor(R.color.blue))
                    }
                    4 -> {
                        binding.cvFriday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvFriday.setTextColor(fetchColor(R.color.blue))
                    }
                    5 -> {
                        binding.cvSaturday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvSaturday.setTextColor(fetchColor(R.color.blue))
                    }
                    6 -> {
                        binding.cvSunday.setCardBackgroundColor(fetchColor(R.color.blue))
                        binding.tvSunday.setTextColor(fetchColor(R.color.blue))
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
            if (navArgs.alarm == null) {
                saveData()
            } else {
                updateData()
            }
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

    var pose = ""
    private fun poseSelect(pose: String) {
        this.pose = pose
    }

    private fun updateData() {

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
        for (i in 0 until 7) {
            if (days[i] == true) {
                repeatDays.add(true)
            } else {
                repeatDays.add(false)
            }
        }

        val challenge: String = if (pose.isEmpty()) {
            "none"
        } else {
            pose
        }

        val alarm =
            Alarm(System.currentTimeMillis().toString(),
                "${timeHour}:${minute}",
                alarmTitle,
                repeatDays,
                challenge,
                false,
                0,
                viewModel.getUserId() ?: FirebaseAuth.getInstance().currentUser!!.uid
            )

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
        }
    }

    override fun bindViewBinding(view: View): FragmentAlarmDetailsBinding {
        return FragmentAlarmDetailsBinding.bind(view)
    }

}