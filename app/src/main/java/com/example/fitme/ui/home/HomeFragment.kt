package com.example.fitme.ui.home

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.fitme.R
import com.example.fitme.camera.CameraSource
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.core.ui.widgets.MainToolbar
import com.example.fitme.data.models.Device
import com.example.fitme.databinding.FragmentHomeBinding
import com.example.fitme.tf.ml.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseNavFragment<HomeViewModel, FragmentHomeBinding>() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    override val viewModel: HomeViewModel by viewModel()

    private var modelPos: Int = 1
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = false
    private var device = Device.CPU

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                ErrorDialog.newInstance("The camera permission is needed")
                    .show(childFragmentManager, FRAGMENT_DIALOG)
            }
        }

    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            changeModel(position)
        }
    }

    private var changeDeviceListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeDevice(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private var changeTrackerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            changeTracker(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private var setClassificationListener =
        CompoundButton.OnCheckedChangeListener { _, isChecked ->
            showClassificationResult(isChecked)
            isClassifyPose = isChecked
            isPoseClassifier()
        }

    private fun isPoseClassifier() {
        cameraSource?.setClassifier(if (isClassifyPose) context?.let { PoseClassifier.create(it) } else null)
    }

    private fun changeTracker(position: Int) {
        cameraSource?.setTracker(
            when (position) {
                1 -> TrackerType.BOUNDING_BOX
                2 -> TrackerType.KEYPOINTS
                else -> TrackerType.OFF
            }
        )
    }

    private fun changeDevice(position: Int) {
        val targetDevice = when (position) {
            0 -> Device.CPU
            1 -> Device.GPU
            else -> Device.NNAPI
        }
        if (device == targetDevice) return
        device = targetDevice
        createPoseEstimator()
    }

    private fun changeModel(position: Int) {
        if (modelPos == position) return
        modelPos = position
        createPoseEstimator()
    }

    private fun createPoseEstimator() {
        // For MoveNet MultiPose, hide score and disable pose classifier as the model returns
        // multiple Person instances.
        val poseDetector = when (modelPos) {
            0 -> {
                // MoveNet Lightning (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(requireContext(), device, ModelType.Lightning)
            }
            1 -> {
                // MoveNet Thunder (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                MoveNet.create(requireContext(), device, ModelType.Thunder)
            }
            2 -> {
                // MoveNet (Lightning) MultiPose
                showPoseClassifier(false)
                showDetectionScore(false)
                // Movenet MultiPose Dynamic does not support GPUDelegate
                if (device == Device.GPU) {
//                    showToast(getString(R.string.tfe_pe_gpu_error))
                }
                showTracker(true)
                MoveNetMultiPose.create(
                    requireContext(),
                    device,
                    Type.Dynamic
                )
            }
            3 -> {
                // PoseNet (SinglePose)
                showPoseClassifier(true)
                showDetectionScore(true)
                showTracker(false)
                PoseNet.create(requireContext(), device)
            }
            else -> {
                null
            }
        }
        poseDetector?.let { detector ->
            cameraSource?.setDetector(detector)
        }
    }

    // Show/hide the pose classification option.
    private fun showPoseClassifier(isVisible: Boolean) {
//        vClassificationOption.visibility = if (isVisible) View.VISIBLE else View.GONE
//        if (!isVisible) {
//            swClassification.isChecked = false
//        }
    }

    // Show/hide the detection score.
    private fun showDetectionScore(isVisible: Boolean) {}

    // Show/hide classification result.
    private fun showClassificationResult(isVisible: Boolean) {}

    // Show/hide the tracking options.
    private fun showTracker(isVisible: Boolean) {}

    // Open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(binding.surfaceView, object : CameraSource.CameraSourceListener {
                        override fun onFPSListener(fps: Int) {

                        }

                        override fun onDetectedInfo(
                            personScore: Float?,
                            poseLabels: List<Pair<String, Float>>?
                        ) {
//                            tvScore.text = getString(R.string.tfe_pe_tv_score, personScore ?: 0f)
                            poseLabels?.sortedByDescending { it.second }?.let {
//                                tvClassificationValue1.text = getString(
//                                    R.string.tfe_pe_tv_classification_value,
//                                    convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                                )
//                                tvClassificationValue2.text = getString(
//                                    R.string.tfe_pe_tv_classification_value,
//                                    convertPoseLabels(if (it.size >= 2) it[1] else null)
//                                )
//                                tvClassificationValue3.text = getString(
//                                    R.string.tfe_pe_tv_classification_value,
//                                    convertPoseLabels(if (it.size >= 3) it[2] else null)
//                                )
                            }
                        }

                    }).apply {
                        prepareCamera()
                    }
                isPoseClassifier()
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return activity?.checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun convertPoseLabels(pair: Pair<String, Float>?): String {
        if (pair == null) return "empty"
        return "${pair.first} (${String.format("%.2f", pair.second)})"
    }

    override fun initView() {
        super.initView()

        initSpinner()
        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
    }

    private fun requestPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }

    private fun initSpinner() {
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.tfe_pe_models_array,
//            android.R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner
//            spnModel.adapter = adapter
//            spnModel.onItemSelectedListener = changeModelListener
//        }
//
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.tfe_pe_device_name, android.R.layout.simple_spinner_item
//        ).also { adaper ->
//            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//            spnDevice.adapter = adaper
//            spnDevice.onItemSelectedListener = changeDeviceListener
//        }
//
//        ArrayAdapter.createFromResource(
//            this,
//            R.array.tfe_pe_tracker_array, android.R.layout.simple_spinner_item
//        ).also { adaper ->
//            adaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//            spnTracker.adapter = adaper
//            spnTracker.onItemSelectedListener = changeTrackerListener
//        }
    }

    override fun initViewModel() {
        super.initViewModel()

    }

    override fun initListeners() {
        super.initListeners()

    }

    class ErrorDialog : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                .setMessage(requireArguments().getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // do nothing
                }
                .create()

        companion object {

            @JvmStatic
            private val ARG_MESSAGE = "message"

            @JvmStatic
            fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
                arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        openCamera()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        val view = FragmentHomeBinding.inflate(inflater, container, false)
//        view.toolbar.bind(
//            leftButton = MainToolbar.ActionInfo(
//                onClick = {
//                    requireActivity().onBackPressed()
//                }
//            )
//        )
        return view
    }

    override fun bindViewBinding(view: View): FragmentHomeBinding {
        return FragmentHomeBinding.bind(view)
    }

}