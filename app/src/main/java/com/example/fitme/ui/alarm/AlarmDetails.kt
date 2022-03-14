package com.example.fitme.ui.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.databinding.FragmentAlarmDetailsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AlarmDetails : BaseFragment<AlarmViewModel, FragmentAlarmDetailsBinding>() {

    override val viewModel: AlarmViewModel by viewModel()
    private val navArgs: AlarmDetailsArgs by navArgs()

    private var timeHourOfDay = 0
    private var minute = 0

    override fun initViewModel() {
        super.initViewModel()
    }

    override fun initView() {
        super.initView()

        setData()
    }

    private fun setData() {
        val calendar = Calendar.getInstance()
        binding.time.hour = calendar.get(Calendar.HOUR)
        binding.time.minute = calendar.get(Calendar.MINUTE)

        val data = navArgs.alarm ?: return
        binding.etAlarmName.setText(data.title)
    }

    override fun initListeners() {
        super.initListeners()

        binding.time.setOnClickListener {
            setTime()
        }
    }

    private fun setTime() {
        timeHourOfDay = binding.time.hour
        minute = binding.time.minute

//        val calendar = Calendar.getInstance()
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