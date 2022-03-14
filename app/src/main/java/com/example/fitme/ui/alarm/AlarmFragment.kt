package com.example.fitme.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.FragmentAlarmBinding
import com.example.fitme.managers.MyAlarmManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmFragment : BaseFragment<AlarmViewModel, FragmentAlarmBinding>() {

    override val viewModel: AlarmViewModel by viewModel()

    private val alarmList = ArrayList<Alarm>()

/*   Begin initViewModel */

    override fun initViewModel() {
        super.initViewModel()

    }

/* End initViewModel */


/* Begin initView */


    override fun initView() {
        super.initView()

        initAlarmList()
    }

    private fun initAlarmList() {
        for (i in 0 until 10) {
            alarmList.add(Alarm("${i + i % (i + 1) * 2}",
                System.currentTimeMillis().plus(40000),
                "Alarm ${i.plus(1)}",
                arrayOf((i + 1) % 6, (i + 2) % 6),
                false,
                null,
                false)
            )
        }

        val adapter = AlarmListRecycler(alarmList, this::onAlarmClick, this::onSwitchChecked)

        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

/* End InitView */

/* Begin Listeners */

    override fun initListeners() {
        super.initListeners()

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(AlarmFragmentDirections.actionAlarmFragmentToAlarmDetails())
        }

    }

    private fun onAlarmClick(alarm: Alarm) {
        findNavController().navigate(AlarmFragmentDirections.actionAlarmFragmentToAlarmDetails(alarm))
    }

    private fun onSwitchChecked(title: String, time: Long, checked: Boolean) {
        if (checked) {
            MyAlarmManager.setAlarm3(requireContext(), "id", title, time)
        } else {
            MyAlarmManager.cancelAlarm(requireContext(), 1)
        }
    }


/* End Listeners */

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentAlarmBinding {
        return FragmentAlarmBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentAlarmBinding {
        return FragmentAlarmBinding.bind(view)
    }

}
