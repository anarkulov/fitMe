package com.example.fitme.ui.home

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.fitme.R
import com.example.fitme.camera.CameraSource
import com.example.fitme.core.ui.BaseNavFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.BodyPart
import com.example.fitme.data.models.Device
import com.example.fitme.data.models.KeyPoint
import com.example.fitme.databinding.FragmentHomeBinding
import com.example.fitme.tf.ml.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class HomeFragment : BaseNavFragment<HomeViewModel, FragmentHomeBinding>() {
    companion object {
        private const val FRAGMENT_DIALOG = "dialog"
    }

    override val viewModel: HomeViewModel by viewModel()

    private val myTag = "HomeFragment"

    private var modelPos: Int = 0
    private var cameraSource: CameraSource? = null
    private var isClassifyPose = true
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

    private var cameraSourceListener = object : CameraSource.CameraSourceListener {
        override fun onFPSListener(fps: Int) {

        }

        override fun onDetectedInfo(
            personScore: Float?,
            keyPoints: List<KeyPoint>?,
            poseLabels: List<Pair<String, Float>>?,
        ) {
            activity?.runOnUiThread {
                binding.tvScore.text = getString(R.string.tv_score, personScore ?: 0f)
            }
            poseLabels?.sortedByDescending { it.second }?.let {
                activity?.runOnUiThread {
//                    binding.tvFirstClassification.text = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                    binding.tvSecondClassification.text = convertPoseLabels(if (it.size >= 2) it[1] else null)
//                    binding.tvThirdClassification.text = convertPoseLabels(if (it.size >= 3) it[2] else null)
                }
            }

            keyPoints?.let {
                if (personScore == null || personScore <= 0.4) return@let
//                Log.d("onDetectedInfo: keyPoints - $keyPoints, ", myTag)
//                val leftElbow = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_ELBOW
//                }
//
//                val right_elbow = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.RIGHT_ELBOW
//                }
//
//                val left_wrist = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_WRIST
//                }
//
//                val right_wrist= it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.RIGHT_WRIST
//                }
//
//                val left_shoulder = it.lastOrNull { keyPoint ->
//                    keyPoint.bodyPart == BodyPart.LEFT_SHOULDER
//                }
//
//                val right_shoulder = it.lastOrNull { keyPoint -> keyPoint.bodyPart == BodyPart.RIGHT_SHOULDER }
//
//                calculatePushUpAngle(leftElbow, right_elbow, left_wrist, right_wrist, left_shoulder, right_shoulder)

                calculate(keyPoints)
            }


//            poseByKeyPoints?.let {
//                Log.d("onDetectedInfo: poseByKeyPoints - $poseByKeyPoints, ", myTag)
//                activity?.runOnUiThread {
////                    binding.tvFirstClassification.text = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
////                    binding.tvSecondClassification.text = convertPoseLabels(if (it.size >= 2) it[1] else null)
////                    binding.tvThirdClassification.text = convertPoseLabels(if (it.size >= 3) it[2] else null)
//                }
//            }
        }
    }

    private var changeModelListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long,
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

    // test
    fun calculateAngle(a: Float, b: Float, c: Float): Double {

//        val radians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        var angle = abs(radians * 180.0/ PI)
//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }

        return 0.0
    }

    //   biceps

    private var counter = 0
    private fun calculate(keyPoints: List<KeyPoint>) {

        val rightWrist = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_WRIST
        }

        val rightElbow = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_ELBOW
        }

        val rightShoulder = keyPoints.lastOrNull { keyPoint ->
            keyPoint.bodyPart == BodyPart.RIGHT_SHOULDER
        }

        if (rightElbow == null || rightShoulder == null|| rightWrist == null) return
        if (rightElbow.score < 0.4 || rightShoulder.score < 0.4 || rightWrist.score < 0.4) return

        val rightWristY = rightWrist.coordinate.y
        val rightWristX = rightWrist.coordinate.x

        val rightElbowY = rightElbow.coordinate.y
        val rightElbowX = rightElbow.coordinate.x

        val rightShoulderY = rightShoulder.coordinate.y
        val rightShoulderX = rightShoulder.coordinate.x

        val radians =
            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))

        val angle = abs(radians * (180.0 / PI))

//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }

        if (angle > 170 && angle < 200) {
            isUp = true
            isDown = false
        }

        if (angle > 25 && angle < 45) {
            if (isUp) {
                counter = binding.tvCounter.text.toString().toInt()
                counter += 1
                activity?.runOnUiThread {
                    binding.tvCounter.text = counter.toString()
                }
            }
            isDown = true
            isUp = false
        }

        Log.d("calculateBiceps: $angle", myTag)
    }

    private var isDown = false
    private var isUp = false

    // pushUpAngle
    private fun calculatePushUpAngle(
        leftElbow: KeyPoint?,
        rightElbow: KeyPoint?,
        leftWrist: KeyPoint?,
        rightWrist: KeyPoint?,
        leftShoulder: KeyPoint?,
        rightShoulder: KeyPoint?,
    ) {
        val leftWristY = leftWrist?.coordinate?.y ?: 0.0f
        val leftWristX = leftWrist?.coordinate?.x ?: 0.0f
        val rightWristY = rightWrist?.coordinate?.y ?: 0.0f
        val rightWristX = rightWrist?.coordinate?.x ?: 0.0f

        val leftElbowY = leftElbow?.coordinate?.y ?: 0.0f
        val leftElbowX = leftElbow?.coordinate?.x ?: 0.0f
        val rightElbowY = rightElbow?.coordinate?.y ?: 0.0f
        val rightElbowX = rightElbow?.coordinate?.x ?: 0.0f

        val leftShoulderY = leftShoulder?.coordinate?.y ?: 0.0f
        val leftShoulderX = leftShoulder?.coordinate?.x ?: 0.0f
        val rightShoulderY = rightShoulder?.coordinate?.y ?: 0.0f
        val rightShoulderX = rightShoulder?.coordinate?.x ?: 0.0f

//        val leftRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
        val leftRadians = atan2(leftShoulderY.minus(leftElbowY), leftShoulderX.minus(leftElbowX)) -
                atan2(leftWristY.minus(leftElbowY), leftWristX.minus(leftElbowX))

//        val rightRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
        val rightRadians =
            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))

        var angle = abs(((leftRadians + rightRadians) / 2.0) * (180.0 / PI))

        if (angle > 180.0) {
            angle = 360.0 - angle
        }

        Log.d("calculatePushUpAngle: $angle", myTag)
//
    }
//    fun calculatePushUpAngle(leftElbow: KeyPoint) : Double {
//
//        val radians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        var angle = abs(radians * 180.0/ PI)
//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }
//
//        return angle
//    }

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
                    CameraSource(binding.surfaceView, cameraSourceListener).apply {
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
            ),
            -> {
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

        binding.btnSwitch.setOnClickListener {
            cameraSource?.switchCamera()
        }
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

    override fun onResume() {
        super.onResume()
        cameraSource?.resume()
    }

    override fun onPause() {
        super.onPause()
        cameraSource?.close()
        cameraSource = null
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