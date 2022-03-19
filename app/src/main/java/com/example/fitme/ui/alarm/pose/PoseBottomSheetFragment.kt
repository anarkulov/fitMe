package com.example.fitme.ui.alarm.pose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitme.databinding.FragmentPoseBottomSheetBinding
import com.example.fitme.ui.alarm.AlarmViewModel
import com.example.fitme.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PoseBottomSheetFragment(private val click: (String) -> Unit): BottomSheetDialogFragment() {

    var binding: FragmentPoseBottomSheetBinding? = null
    private val viewModel by viewModel<AlarmViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPoseBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val behavior = BottomSheetBehavior.from(binding!!.container)
        behavior.apply {
            setPeekHeight(Utils.toDp(200f, requireContext()), true)
            state = STATE_EXPANDED
        }
        initAdapter()
    }

    private fun initAdapter() {
        val data = ArrayList<String>()

        data.add("Chair")
        data.add("Cobra")
        data.add("Dog")
        data.add("Tree")
        data.add("Warrior")

        val poseAdapter = PoseBottomSheetAdapter(data, this::poseSelect)
        binding?.recyclerView?.apply {
            adapter = poseAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun poseSelect(pose: String) {
        click(pose)
        dialog?.dismiss()
    }
}
