package com.example.fitme.ui.alarm.exercise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Process
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fitme.R
import com.example.fitme.camera.CameraSource
import com.example.fitme.core.extentions.showToast
import com.example.fitme.core.ui.BaseFragment
import com.example.fitme.core.utils.Log
import com.example.fitme.data.models.ml.BodyPart
import com.example.fitme.data.models.ml.Device
import com.example.fitme.data.models.ml.KeyPoint
import com.example.fitme.databinding.FragmentExerciseBinding
import com.example.fitme.tf.ml.ModelType
import com.example.fitme.tf.ml.MoveNet
import com.example.fitme.tf.ml.PoseClassifier
import com.example.fitme.tf.ml.PoseDetector
import com.example.fitme.ui.alarm.AlarmViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2

class ExerciseFragment : BaseFragment<AlarmViewModel, FragmentExerciseBinding>() {

    override val viewModel: AlarmViewModel by viewModel()

    private val myTag = "ExerciseFragment"

    private var cameraSource: CameraSource? = null
    private var isHandDown = false
    private var isHandUp = false

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openCamera()
            } else {
                showToast("The camera permission is needed")
            }
        }

    private fun createPoseEstimator() {
        val poseDetectors = {
            MoveNet.create(requireContext(), Device.CPU, ModelType.Lightning)
        }
        cameraSource?.setDetector(poseDetectors as PoseDetector)
    }

    // Open camera
    private fun openCamera() {
        if (isCameraPermissionGranted()) {
            if (cameraSource == null) {
                cameraSource =
                    CameraSource(binding.surfaceView, cameraSourceListener).apply {
                        prepareCamera()
                    }
                cameraSource?.setClassifier(context?.let { PoseClassifier.create(it) })
                lifecycleScope.launch(Dispatchers.Main) {
                    cameraSource?.initCamera()
                }
            }
            createPoseEstimator()
        }
    }

    override fun initViewModel() {
        super.initViewModel()
    }

    private fun isCameraPermissionGranted(): Boolean {
        return activity?.checkPermission(
            Manifest.permission.CAMERA,
            Process.myPid(),
            Process.myUid()
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun initView() {
        super.initView()

        if (!isCameraPermissionGranted()) {
            requestPermission()
        }
    }

    override fun initListeners() {
        super.initListeners()
    }

    //   Biceps
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

        if (rightElbow == null || rightShoulder == null || rightWrist == null) return
//        if (rightElbow.score < 0.3 || rightShoulder.score < 0.4 || rightWrist.score < 0.3) return

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
            isHandUp = true
            isHandDown = false
        }

        if (angle > 25 && angle < 45) {
            if (isHandUp) {
                counter = binding.tvCounter.text.toString().toInt()
                counter += 1
                activity?.runOnUiThread {
                    binding.tvCounter.text = counter.toString()
                }
            }
            isHandDown = true
            isHandUp = false
        }

        Log.d("calculateBiceps: $angle", myTag)
    }

// pushUpAngle
//    private fun calculatePushUpAngle(keyPoints: List<KeyPoint>) {
//
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

//        val leftWristY = leftWrist?.coordinate?.y ?: 0.0f
//        val leftWristX = leftWrist?.coordinate?.x ?: 0.0f
//        val rightWristY = rightWrist?.coordinate?.y ?: 0.0f
//        val rightWristX = rightWrist?.coordinate?.x ?: 0.0f
//
//        val leftElbowY = leftElbow?.coordinate?.y ?: 0.0f
//        val leftElbowX = leftElbow?.coordinate?.x ?: 0.0f
//        val rightElbowY = rightElbow?.coordinate?.y ?: 0.0f
//        val rightElbowX = rightElbow?.coordinate?.x ?: 0.0f
//
//        val leftShoulderY = leftShoulder?.coordinate?.y ?: 0.0f
//        val leftShoulderX = leftShoulder?.coordinate?.x ?: 0.0f
//        val rightShoulderY = rightShoulder?.coordinate?.y ?: 0.0f
//        val rightShoulderX = rightShoulder?.coordinate?.x ?: 0.0f
//
//        val leftRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        val leftRadians = atan2(leftShoulderY.minus(leftElbowY), leftShoulderX.minus(leftElbowX)) -
//                atan2(leftWristY.minus(leftElbowY), leftWristX.minus(leftElbowX))
//
//        val rightRadians = atan2(c[1]-b[1], c[0] - b[0]) - atan2(a[1]-b[1], a[0] - b[0])
//        val rightRadians =
//            atan2(rightShoulderY.minus(rightElbowY), rightShoulderX.minus(rightElbowX)) -
//                    atan2(rightWristY.minus(rightElbowY), rightWristX.minus(rightElbowX))
//
//        var angle = abs(((leftRadians + rightRadians) / 2.0) * (180.0 / PI))
//
//        if (angle > 180.0) {
//            angle = 360.0 - angle
//        }
//
//        Log.d("calculatePushUpAngle: $angle", myTag)
//
//    }

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

    private var cameraSourceListener = object : CameraSource.CameraSourceListener {

        override fun onFPSListener(fps: Int) {}

        override fun onDetectedInfo(
            personScore: Float?,
            keyPoint: List<KeyPoint>?,
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

            keyPoint?.let {
                if (personScore == null || personScore <= 0.3) return@let
                calculate(keyPoint)
            }

//            poseByKeyPoints?.let {
//                Log.d("onDetectedInfo: poseByKeyPoints - $poseByKeyPoints, ", myTag)
//                activity?.runOnUiThread {
//                    binding.tvFirstClassification.text = convertPoseLabels(if (it.isNotEmpty()) it[0] else null)
//                    binding.tvSecondClassification.text = convertPoseLabels(if (it.size >= 2) it[1] else null)
//                    binding.tvThirdClassification.text = convertPoseLabels(if (it.size >= 3) it[2] else null)
//                }
//            }
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
    ): FragmentExerciseBinding {
        return FragmentExerciseBinding.inflate(inflater, container, false)
    }

    override fun bindViewBinding(view: View): FragmentExerciseBinding {
        return FragmentExerciseBinding.bind(view)
    }
}