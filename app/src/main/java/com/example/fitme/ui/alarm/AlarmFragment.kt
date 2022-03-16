package com.example.fitme.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.core.extentions.visible
import com.example.fitme.core.network.result.Status
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.Alarm
import com.example.fitme.databinding.FragmentAlarmBinding
import com.example.fitme.managers.MyAlarmManager
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmFragment : BaseFragment<AlarmViewModel, FragmentAlarmBinding>() {

    override val viewModel: AlarmViewModel by viewModel()

    private val alarmList = ArrayList<Alarm>()
    private val alarmAdapter = AlarmListRecycler(alarmList, this::onAlarmClick, this::onSwitchChecked)
    private val myTag = "AlarmFragment"

/*   Begin initViewModel */

    override fun initViewModel() {
        super.initViewModel()

        viewModel.loading.observe(this) {
            binding.loading.visible = it
        }

        viewModel.getAlarmList().observe(this) { response ->
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
                        alarmAdapter.updateItems(it)
                    }
                }
            }
        }
    }

/* End initViewModel */


/* Begin initView */


    override fun initView() {
        super.initView()

        initAlarmList()
    }

    private fun initAlarmList() {
        binding.recyclerView.apply {
            this.adapter = alarmAdapter
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

    private fun onSwitchChecked(alarm: Alarm, checked:Boolean) {
        updateOnFirebase(alarm)

        if (checked) {
            MyAlarmManager.scheduleAlarm(requireContext(), alarm)
        } else {
            MyAlarmManager.cancelAlarm3(requireContext())
        }
    }

    private fun updateOnFirebase(alarm: Alarm) {
        viewModel.updateAlarm(alarm).observe(this) { response ->
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
                        Log.d("SUCCESS updated on db", myTag)
                    }
                }
            }
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
